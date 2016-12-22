package sexy.kome.spider.processer.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.util.MD5FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.Spider;
import sexy.kome.spider.processer.Processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * Created by Hack on 2016/11/27.
 */
public class ImageProcessor implements Processor {
    private static final Logger RUN_LOG = Logger.getLogger(ImageProcessor.class);
    private static final long DEFAULT_MIN_IMAGE_SIZE = 40 * 1024; // 默认最小下载128K的图片
    private static final long DEFAULT_MAX_IMAGE_SIZE = 1024 * 1024 * 10; // 默认最小下载10M的图片
    private static final String DEFAULT_IMAGE_SUFFIX = ".jpg,.jpeg,.png,.gif";

    private static final String STORE_IMG_DIR = ConfigHelper.get("spider.img.dir");
    private static final long MIN_IMAGE_SIZE = ConfigHelper.containsKey("spider.img.min.size") ?
            Long.valueOf(ConfigHelper.get("spider.img.min.size")) : DEFAULT_MIN_IMAGE_SIZE;
    private static final long MAX_IMAGE_SIZE = ConfigHelper.containsKey("spider.img.max.size") ?
            Long.valueOf(ConfigHelper.get("spider.img.max.size")) : DEFAULT_MAX_IMAGE_SIZE;

    private Spider context;

    public ImageProcessor (Spider context) {
        this.context = context;
    }

    @Override
    public void process(Document document) {
        document.select("img[src]").forEach(image -> {
            try {
                String url = image.attr("abs:src");
                if (url.length() < 100 && url.contains(".") && DEFAULT_IMAGE_SUFFIX.contains(url.substring(url.lastIndexOf("."))) && !context.getContainer().hasVisitedImageURL(url)) {
                    RUN_LOG.info(String.format("Start-Process-URL [url=%s]", url));
                    File targetImageFile = rename2md5hex(download(url));

                    if (null != targetImageFile) {
                        context.getContainer().saveVisitedImageURL(url);
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
            FileOutputStream outputStream = new FileOutputStream(downloadFile);

            byte[] buffer = new byte[1024];
            int readSize = -1;
            int totalSize = 0;
            while ((readSize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readSize);
                totalSize += readSize;
            }

            outputStream.flush();
            outputStream.getFD().sync();

            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            IOUtils.close(urlConnection);

            // 校验文件大小
            if (totalSize < MIN_IMAGE_SIZE || totalSize > MAX_IMAGE_SIZE) {
                RUN_LOG.warn(String.format("Image-Size-Out-Min-Bounds [current-size=%d, target-min-size=%d, target-max-size=%d]", totalSize, MIN_IMAGE_SIZE, MAX_IMAGE_SIZE));
                downloadFile.delete();
                return null;
            } else {
                RUN_LOG.info(String.format("Image-Download [url=%s, file=%s, size=%d]", url, downloadFile.getName(), downloadFile.length()));
                return downloadFile;
            }
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
            return null;
        }
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
}
