package sexy.kome.spider.processer.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.Spider;
import sexy.kome.spider.processer.Processor;

import java.io.*;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Hack on 2016/11/27.
 */
public class ImageProcessor implements Processor {
    private static final Logger RUN_LOG = Logger.getLogger(ImageProcessor.class);
    private static final long DEFAULT_MIN_IMAGE_SIZE = 10*1024; // 默认最小下载128K的图片
    private static final String DEFAULT_IMAGE_SUFFIX = "jpg,jpeg,png,gif";

    private static final String STORE_IMG_DIR = ConfigHelper.get("spider.img.dir");
    private static final long MIN_IMAGE_SIZE = ConfigHelper.containsKey("spider.img.min.size") ?
            Long.valueOf(ConfigHelper.get("spider.img.min.size")) : DEFAULT_MIN_IMAGE_SIZE;

    @Override
    public void process(Document document) {
        document.select("img[src]").forEach(image -> {
            try {
                String targetImageURL = image.attr("abs:src");
                if (targetImageURL.length() <= Spider.MAX_URL_LENGTH) {
                    download(targetImageURL);
                }
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        });
    }

    private void download(String url) throws Exception {
        RUN_LOG.info(String.format("Process-Image [url=%s]", url));

        String imageSuffix = url.substring(url.lastIndexOf(".") + 1);
        if (StringUtils.isEmpty(imageSuffix) || !DEFAULT_IMAGE_SUFFIX.contains(imageSuffix.toLowerCase())) {
            RUN_LOG.warn(String.format("Image-Suffix-Wrong [Target-Suffix=%s, Current-Suffix=%s]", DEFAULT_IMAGE_SUFFIX, imageSuffix));
            return;
        }

        File tempFile = new File(STORE_IMG_DIR.concat("/").concat(UUID.randomUUID().toString()).concat(".").concat(imageSuffix));
        int fileSize = transfer(new URL(url).openStream(), tempFile);
        RUN_LOG.info(String.format("Image-Download [image=%s, size=%d]", tempFile.getName(), fileSize));
        if (fileSize < MIN_IMAGE_SIZE) {
            tempFile.delete();
            RUN_LOG.warn(String.format("Image-Size-Wrong And Deleted [Target-Min-Size=%d, Current-Size=%d]", MIN_IMAGE_SIZE, fileSize));
            return;
        }

        // 以md5重命名
        String md5hex = md5hex(tempFile);
        File targetFile = new File(STORE_IMG_DIR.concat("/").concat(md5hex).concat(".").concat(imageSuffix));
        if (targetFile.exists() && !tempFile.getName().equalsIgnoreCase(targetFile.getName())) {
            tempFile.delete();
            RUN_LOG.warn(String.format("Image-Exists [file=%s]", targetFile.getName()));
            return;
        }

        // 以MD5重命名
        if (!tempFile.getName().equalsIgnoreCase(targetFile.getName())) {
            tempFile.renameTo(targetFile);
        }
    }

    private int transfer(InputStream inputStream, File targetFile) throws Exception {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        OutputStream outputStream = new FileOutputStream(targetFile);
        byte[] buffer = new byte[1024];
        int readSize = -1;
        int total = 0;
        while ((readSize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readSize);
            total += readSize;
        }
        outputStream.flush();
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
        return total;
    }

    private String md5hex(File file) throws Exception {
        InputStream inputStream = new FileInputStream(file);
        String md5hex = DigestUtils.md5Hex(inputStream);
        IOUtils.closeQuietly(inputStream);
        return md5hex;
    }

    public static void main(String[] args) throws Exception {
        String temp = "http://i.meizitu.net/pfiles/img/lazy.png";
        new ImageProcessor().download(temp);
    }
}
