package sexy.kome.spider.processer.impl;

import sexy.kome.spider.processer.Processor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;

import java.io.*;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Hack on 2016/11/27.
 */
public class ImageProcessor implements Processor {
    private static final Logger RUN_LOG = Logger.getLogger(ImageProcessor.class);
    private static final long DEFAULT_MIN_IMAGE_SIZE = 10*1024; // 默认最小下载128K的图片
    private static final long DEFAULT_MAX_IMAGE_SIZE = 1*1024*1024*1024; // 默认最大下载1G的图片
    private static final String DEFAULT_IMAGE_SUFFIX = "jpg,jpeg,png,gif";

    private static final String STORE_IMG_DIR = ConfigHelper.get("spider.img.dir");
    private static final long MIN_IMAGE_SIZE = ConfigHelper.containsKey("spider.min.image.size") ?
            Long.valueOf(ConfigHelper.get("spider.min.image.size")) : DEFAULT_MIN_IMAGE_SIZE;
    private static final long MAX_IMAGE_SIZE = ConfigHelper.containsKey("spider.max.image.size") ?
            Long.valueOf(ConfigHelper.get("spider.max.image.size")) : DEFAULT_MAX_IMAGE_SIZE;

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

        String imageSuffix = url.substring(url.lastIndexOf(".") + 1);
        if (StringUtils.isEmpty(imageSuffix) || !DEFAULT_IMAGE_SUFFIX.contains(imageSuffix)) {
            throw new RuntimeException(String.format("Image-Suffix Wrong [Target-Suffix=%s, Current-Suffix=%s]", DEFAULT_IMAGE_SUFFIX, imageSuffix));
        }

        File file = new File(STORE_IMG_DIR.concat("/").concat(UUID.randomUUID().toString()).concat(".").concat(imageSuffix));
        File tempTargetFile = transfer(new URL(url).openStream(), file);
        RUN_LOG.info(String.format("Image-Download [image=%s, size=%d]", file.getName(), tempTargetFile.length()));
        if (tempTargetFile.length() > MAX_IMAGE_SIZE || tempTargetFile.length() < MIN_IMAGE_SIZE) {
            file.deleteOnExit();
            RUN_LOG.warn(String.format("Image-Size Wrong And Deleted [Target-Min-Size=%d, Target-Max-Size=%d, Current-Size=%d]",
                    MIN_IMAGE_SIZE, MAX_IMAGE_SIZE, tempTargetFile.length()));
        }

        // 取文件md5
        InputStream inputStream = new FileInputStream(tempTargetFile);
        String md5hex = DigestUtils.md5Hex(inputStream);
        IOUtils.closeQuietly(inputStream);

        // 以md5重命名
        File targetFile = new File(STORE_IMG_DIR.concat("/").concat(md5hex).concat(".").concat(imageSuffix));
        if (targetFile.exists()) {
            targetFile.deleteOnExit();
            RUN_LOG.warn(String.format("Target File Exists [file=%s]", targetFile.getName()));
        } else {
            transfer(new FileInputStream(tempTargetFile), targetFile);
            tempTargetFile.deleteOnExit();
        }
    }

    private File transfer(InputStream inputStream, File targetFile) throws Exception {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        OutputStream outputStream = new FileOutputStream(targetFile);
        byte[] buffer = new byte[4096];
        int readSize = -1;
        while ((readSize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readSize);
        }
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
        return targetFile;
    }

    public static void main(String[] args) throws Exception {
        String temp = "http://www.meinvh.com/uploads/allimg/160629/1-16062Z9310K17.jpg";
        new ImageProcessor().download(temp);
    }
}
