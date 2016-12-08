package sexy.kome.spider.processer.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.util.MD5FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.processer.Processor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static final long DEFAULT_MIN_IMAGE_SIZE = 10 * 1024; // 默认最小下载128K的图片
    private static final long DEFAULT_MAX_IMAGE_SIZE = 1024 * 1024 * 10; // 默认最小下载10M的图片
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
                String url = image.attr("abs:src");
                if (url.length() < 100 && url.contains(".") && DEFAULT_IMAGE_SUFFIX.contains(url.substring(url.lastIndexOf("."))) && !URL_IMAGE_VISITED.contains(url)) {
                    RUN_LOG.info(String.format("Start-Process-URL [url=%s]", url));
                    File targetImageFile = rename2md5hex(validate(download(url)));

                    if (null != targetImageFile) {
                        URL_IMAGE_VISITED.add(url);
                        RUN_LOG.info(String.format("PUT-IMAGE-URL [VISITED=%d]", URL_IMAGE_VISITED.size()));
                    }
                }
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        });
    }

    public File download(String url) {
        try {
            File downloadFile = new File(getAbsFileName(UUID.randomUUID().toString().concat(url.substring(url.lastIndexOf(".")))));
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setConnectTimeout(3000);
            InputStream inputStream = urlConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream(downloadFile);

            byte[] buffer = new byte[1024];
            int readSize = -1;
            while ((readSize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readSize);
            }

            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            IOUtils.close(urlConnection);
            RUN_LOG.info(String.format("Image-Download [url=%s, file=%s, size=%d]", url, downloadFile.getName(), downloadFile.length()));
            return downloadFile;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private File validate(File file) throws Exception {
        if (file.exists() && (file.length() < MIN_IMAGE_SIZE || file.length() > MAX_IMAGE_SIZE)) {
            file.delete();
            RUN_LOG.warn(String.format("Image-Size-Out-Min-Bounds [current-size=%d, target-min-size=%d, target-max-size=%d]", file.length(), MIN_IMAGE_SIZE, MAX_IMAGE_SIZE));
        }

        if (file.exists()) {
            BufferedImage bufferedImage = ImageIO.read(file);
            if (bufferedImage == null || bufferedImage.getWidth() < MIN_IMAGE_WIDTH || bufferedImage.getHeight() < MIN_IMAGE_HEIGHT) {
                file.delete();
                RUN_LOG.debug(String.format("Image-Size-Out-Min-Bounds [current-width=%d, current-Height=%d, target-width=%s, target-height=%d]",
                        bufferedImage.getWidth(), bufferedImage.getHeight(), MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT));
            }
        }
        if (null != file) {
            BufferedImage bufferedImage = ImageIO.read(file);
            if (bufferedImage == null || bufferedImage.getWidth() < MIN_IMAGE_WIDTH || bufferedImage.getHeight() < MIN_IMAGE_HEIGHT) {
                file.delete();
                RUN_LOG.debug(String.format("Wrong-Size [current-width=%d, current-Height=%d, target-width=%s, target-height=%d]",
                        bufferedImage.getWidth(), bufferedImage.getHeight(), MIN_IMAGE_WIDTH, MIN_IMAGE_HEIGHT));
            }
        }
        return file.exists() ? file : null;
    }

    // 按文件MD5值对文件重命名
    private File rename2md5hex(File file) throws Exception {
        if (null != file && file.exists()) {
            File targetMD5File = new File(getAbsFileName(MD5FileUtils.computeMd5ForFile(file).toString().concat(file.getName().substring(file.getName().lastIndexOf(".")))));
            if (targetMD5File.exists()) {
                file.delete();
                RUN_LOG.debug(String.format("File-exists [uuid-file=%s, md5-file=%s]", file.getName(), targetMD5File.getName()));
            } else {
                file.renameTo(targetMD5File);
                RUN_LOG.debug(String.format("File-Renamed [uuid-file=%s, md5-file=%s]", file.getName(), targetMD5File.getName()));
                return targetMD5File;
            }
        }
        return null;
    }

    private String getAbsFileName(String fileName) {
        return STORE_IMG_DIR.concat("/").concat(StringUtils.isEmpty(fileName) ? UUID.randomUUID().toString() : fileName);
    }

    public static void main(String[] args) throws Exception {
        String temp = "http://www.pp3.cn/uploads/201609/2016092308.jpg";
        new ImageProcessor().download(temp);
    }
}
