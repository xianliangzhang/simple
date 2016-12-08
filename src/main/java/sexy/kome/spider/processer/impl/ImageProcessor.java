package sexy.kome.spider.processer.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.hadoop.hdfs.util.MD5FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.Spider;
import sexy.kome.spider.processer.Processor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Hack on 2016/11/27.
 */
public class ImageProcessor implements Processor {
    private static final Logger RUN_LOG = Logger.getLogger(ImageProcessor.class);
    private static final long DEFAULT_MIN_IMAGE_SIZE = 128 * 1024; // 默认最小下载128K的图片
    private static final long DEFAULT_MAX_IMAGE_SIZE = 1024 * 1024; // 默认最小下载1024K的图片
    private static final long DEFAULT_MIN_IMAGE_WIDTH = 600; // 默认最小下载1024K的图片
    private static final long DEFAULT_MIN_IMAGE_HEIGHT = 400; // 默认最小下载1024K的图片
    private static final String DEFAULT_IMAGE_SUFFIX = ".jpg,.jpeg,.png,.gif";
    private static final Set<String> URL_IMAGE_VISITED = new HashSet<String>();

    private static final String STORE_IMG_DIR = ConfigHelper.get("spider.img.dir");
    private static final long MIN_IMAGE_SIZE = ConfigHelper.containsKey("spider.img.min.size") ?
            Long.valueOf(ConfigHelper.get("spider.img.min.size")) : DEFAULT_MIN_IMAGE_SIZE;
    private static final long MAX_IMAGE_SIZE = ConfigHelper.containsKey("spider.img.max.size") ?
            Long.valueOf(ConfigHelper.get("spider.img.max.size")) : DEFAULT_MAX_IMAGE_SIZE;

    private static final long MIN_IMAGE_WIDTH = ConfigHelper.containsKey("spider.img.min.width") ?
            Long.valueOf(ConfigHelper.get("spider.img.min.width")) : DEFAULT_MIN_IMAGE_WIDTH;
    private static final long MIN_IMAGE_HEIGHT = ConfigHelper.containsKey("spider.img.max.height") ?
            Long.valueOf(ConfigHelper.get("spider.img.max.height")) : DEFAULT_MIN_IMAGE_HEIGHT;

    @Override
    public void process(Document document) {
        document.select("img[src]").forEach(image -> {
            try {
                String targetImageURL = image.attr("abs:src");
                File targetImageFile = rename2md5hex(validate(download(targetImageURL)));

                if (null != targetImageFile) {
                    URL_IMAGE_VISITED.add(targetImageURL);
                    RUN_LOG.info(String.format("PUT-IMAGE-URL [VISITED=%d]", URL_IMAGE_VISITED.size()));
                }
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        });
    }

    private File download(String url) throws Exception {
        if (url.length() <= Spider.MAX_URL_LENGTH && !URL_IMAGE_VISITED.contains(url) &&
                url.contains(".") && DEFAULT_IMAGE_SUFFIX.contains(url.substring(url.lastIndexOf(".")))) {
            String tempFileName= getAbsFileName(UUID.randomUUID().toString().concat(url.substring(url.lastIndexOf("."))));
            File tempFile = new File(tempFileName);

            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setConnectTimeout(1000);
            InputStream inputStream = urlConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream(tempFile);
            try {
                if (inputStream.available() > MIN_IMAGE_SIZE && inputStream.available() < MAX_IMAGE_SIZE) {
                    byte[] buffer = new byte[1024];
                    int readSize = -1;
                    while ((readSize = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, readSize);
                    }
                    outputStream.flush();
                }
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            } finally {
                inputStream.close();
                outputStream.close();
                IOUtils.close(urlConnection);
            }
            return tempFile.exists() ? tempFile : null;
        }
        return null;
    }

    private File validate(File file) throws Exception {
        if (null != file) {
            if (file.length() < MIN_IMAGE_SIZE || file.length() > MAX_IMAGE_SIZE) {
                file.delete();
                return null;
            }

            BufferedImage bufferedImage = ImageIO.read(file);
            if (bufferedImage == null || bufferedImage.getWidth() < MIN_IMAGE_WIDTH || bufferedImage.getHeight() < MIN_IMAGE_HEIGHT) {
                file.delete();
                return null;
            }
        }
        return file;
    }

    // 按文件MD5值对文件重命名
    private File rename2md5hex(File file) throws Exception {
        if (null != file) {
            File targetMD5File = new File(getAbsFileName(MD5FileUtils.computeMd5ForFile(file).toString().concat(file.getName().substring(file.getName().lastIndexOf(".")))));
            if (targetMD5File.exists()) {
                file.delete();
                RUN_LOG.info(String.format("FILE-EXISTS [md5=%s]", targetMD5File.getName()));
                return null;
            } else {
                file.renameTo(targetMD5File);
                return targetMD5File;
            }
        }
        return file;
    }

    private String getAbsFileName(String fileName) {
        return STORE_IMG_DIR.concat("/").concat(fileName);
    }

    public static void main(String[] args) throws Exception {
        String temp = "http://t1.mmonly.com/mmonly/2014/201412/301/slt.jpg";

        try {
        ImageProcessor processor = new ImageProcessor();
        File tt = processor.rename2md5hex(processor.validate(processor.download(temp)));
        System.out.println(tt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
