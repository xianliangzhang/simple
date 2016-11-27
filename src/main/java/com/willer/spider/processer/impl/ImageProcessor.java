package com.willer.spider.processer.impl;

import com.willer.common.ConfigHelper;
import com.willer.spider.processer.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Hack on 2016/11/27.
 */
public class ImageProcessor implements Processor {
    private static final Logger RUN_LOG = Logger.getLogger(ImageProcessor.class);
    private static final long DEFAULT_MIN_IMAGE_SIZE = 100*1024; // 默认最小下载128K的图片
    private static final long DEFAULT_MAX_IMAGE_SIZE = 1*1024*1024*1024; // 默认最大下载1G的图片
    private static final String DEFAULT_IMAGE_SUFFIX = "jpg,jpeg,png,gif";

    private static final String STORE_IMG_DIR = ConfigHelper.get("spider.img.dir");
    private static final long MIN_IMAGE_SIZE = ConfigHelper.containsKey("spider.min.image.size") ?
            Long.valueOf(ConfigHelper.get("spider.min.image.size")) : DEFAULT_MIN_IMAGE_SIZE;
    private static final long MAX_IMAGE_SIZE = ConfigHelper.containsKey("spider.max.image.size") ?
            Long.valueOf(ConfigHelper.get("spider.max.image.size")) : DEFAULT_MAX_IMAGE_SIZE;
    private static final Set<String> IMAGE_SUFFIX = new HashSet<String>(Arrays.asList( ConfigHelper.containsKey("spider.image.suffix") ?
            ConfigHelper.get("spider.image.suffix") : DEFAULT_IMAGE_SUFFIX ));

    static {
        File tempImgDir = new File(STORE_IMG_DIR);
        if (!tempImgDir.exists()) {
            tempImgDir.mkdirs();
        }
    }

    @Override
    public void process(Document document) {
        document.select("img[src]").forEach(image -> {
            try {
                download(image.attr("abs:src"));
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        });
    }

    private void download(String url) throws Exception {
        RUN_LOG.info(String.format("Start Process Image [url=%s]", url));

        String fileName = url.substring(url.lastIndexOf("/")+1).trim();
//        String imageSuffix = fileName.substring(fileName.lastIndexOf("."));
//        if (StringUtils.isEmpty(imageSuffix)) {
//            throw new RuntimeException(String.format("Image-Suffix Wrong [Target-Suffix=%s, Current-Suffix=%s]", IMAGE_SUFFIX, imageSuffix));
//        }

        File file = new File(STORE_IMG_DIR.concat("/").concat(fileName));
        if (file.exists()) {
            file = new File(STORE_IMG_DIR.concat("/").concat(UUID.randomUUID().toString()).concat(fileName));
        }

        InputStream inputStream = new URL(url.trim()).openStream();
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int readSize = -1;
        int totalReadSize = 0;
        while ((readSize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readSize);
            totalReadSize += readSize;
        }
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);

        RUN_LOG.info(String.format("Image-Download [image=%s, size=%d]", fileName, totalReadSize));
        if (totalReadSize > MAX_IMAGE_SIZE || totalReadSize < MIN_IMAGE_SIZE) {
            file.deleteOnExit();
            RUN_LOG.warn(String.format("Image-Size Wrong And Deleted [Target-Min-Size=%d, Target-Max-Size=%d, Current-Size=%d]",
                    MIN_IMAGE_SIZE, MAX_IMAGE_SIZE, totalReadSize));
        }
    }

    public static void main(String[] args) throws Exception {
        String temp = "http://www.meinvh.com/uploads/allimg/160629/1-16062Z9310K17.jpg";
        new ImageProcessor().download(temp);
    }
}
