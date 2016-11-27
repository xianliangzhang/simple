package com.willer.spider;

import com.willer.spider.processer.Processor;
import com.willer.spider.processer.impl.ImageProcessor;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Hack on 2016/11/27.
 */
public class Spider {
    private static final Logger RUN_LOG = Logger.getLogger(Spider.class);
    private static final String DEFAULT_SOURCE_URL = "http://qihuayao.com/article-19978-1.html";
    private static final Set<Processor> DOCUMENT_PROCESSORS = new HashSet<Processor>();
    static {
        DOCUMENT_PROCESSORS.add(new ImageProcessor());
    }

    static class ProcessThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String url = URLContainer.get();
                    RUN_LOG.info(String.format("Start Process URL [url=%s]", url));
                    Document document = Jsoup.connect(url).timeout(5000).get();

                    document.select("a[href]").forEach(link -> {
                        URLContainer.put(link.attr("abs:href").trim());
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
        String srcUrl = args.length > 0 ? args[0] : DEFAULT_SOURCE_URL;
        URLContainer.put(srcUrl);

        ProcessThread PROCESS_THREAD = new ProcessThread();
        PROCESS_THREAD.start();
    }
}
