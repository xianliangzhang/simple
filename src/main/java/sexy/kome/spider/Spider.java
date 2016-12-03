package sexy.kome.spider;

import sexy.kome.spider.processer.Processor;
import sexy.kome.spider.processer.impl.ImageProcessor;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hack on 2016/11/27.
 */
public class Spider {
    private static final Logger RUN_LOG = Logger.getLogger(Spider.class);
    private static final String DEFAULT_SOURCE_URL = "http://qihuayao.com/article-19978-1.html";
    private static final String DEFAULT_SOURCE_URL_CONFIG_KEY = "spider.source.url";
    private static final Set<Processor> DOCUMENT_PROCESSORS = new HashSet<Processor>();
    private static final Queue<String> UNVISITED_URLS_QUEUE = new LinkedBlockingQueue<>();
    private static final Queue<String> VISITED_URLS_QUEUE = new LinkedBlockingQueue<String>();

    public static void registerProcessor(Processor processor) {
        DOCUMENT_PROCESSORS.add(new ImageProcessor());
    }

    public static void putURL(String url) {
        if (!UNVISITED_URLS_QUEUE.contains(url.trim()) && !VISITED_URLS_QUEUE.contains(url.trim())) {
            UNVISITED_URLS_QUEUE.offer(url.trim());
            RUN_LOG.info(String.format("Put-Url [url=%s]", url));
        }
    }

    public static String getURL() {
        try {
            String url = ((BlockingQueue<String>) UNVISITED_URLS_QUEUE).take();
            VISITED_URLS_QUEUE.add(url);
            return url;
        } catch (InterruptedException e) {
            RUN_LOG.error(e.getMessage(), e);
            return null;
        }
    }

    static class ProcessThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String url = Spider.getURL();
                    RUN_LOG.info(String.format("Start Process URL [url=%s]", url));
                    Document document = Jsoup.connect(url).timeout(5000).get();

                    document.select("a[href]").forEach(link -> {
                        Spider.putURL(link.attr("abs:href").trim());
                    });

                    DOCUMENT_PROCESSORS.forEach(processor -> {
                        processor.process(document);
                    });
                } catch (Exception e) {
                    RUN_LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            ConfigHelper.load(args[0].trim());
        }

        // 初始化源URL
        Spider.putURL(ConfigHelper.containsKey("spider.source.url") ? ConfigHelper.get("spider.source.url") : DEFAULT_SOURCE_URL);

        // 注册处理器
        Spider.registerProcessor(new ImageProcessor());

        // 启动线程
        ProcessThread PROCESS_THREAD = new ProcessThread();
        PROCESS_THREAD.start();
    }

}
