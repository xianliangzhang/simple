package sexy.kome.spider;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.model.SpiderURL;
import sexy.kome.spider.model.SpiderURLStatus;
import sexy.kome.spider.processer.Processor;
import sexy.kome.spider.processer.impl.ImageProcessor;
import sexy.kome.spider.service.SpiderService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hack on 2016/11/27.
 */
public class Spider {
    private static final Logger RUN_LOG = Logger.getLogger(Spider.class);
    private static final String DEFAULT_SOURCE_URL = "http://www.mzitu.com/38791/4";
    private static final Set<Processor> DOCUMENT_PROCESSORS = new HashSet<Processor>();
    private static final int DEFAULT_QUEUE_SIZE = 1000;
    private static final BlockingDeque<SpiderURL> UNVISITED_URLS = new LinkedBlockingDeque<SpiderURL>(DEFAULT_QUEUE_SIZE);
    private static final Set<URLConsumer> CUSTOMER_THREADS = new HashSet<URLConsumer>();
    public static final SpiderService SPIDER_SERVICE = new SpiderService();

    public static void registerProcessor(Processor processor) {
        DOCUMENT_PROCESSORS.add(new ImageProcessor());
    }

    private static class URLConsumer extends Thread {
        boolean running = true;
        int tid = 0;

        public URLConsumer(int tid) {
            this.tid = tid;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    process(UNVISITED_URLS.take());
                } catch (InterruptedException e) {
                    running = false;
                } catch (Exception e) {
                    RUN_LOG.error(e.getMessage(), e);
                    running = false;
                }
            }
        }

        private void process(SpiderURL spiderURL) {
            try {
                Document document = Jsoup.connect(spiderURL.getUrl().trim()).timeout(5000).get();
                DOCUMENT_PROCESSORS.forEach(processor -> {
                    processor.process(document);
                });

                spiderURL.setStatus(SpiderURLStatus.VISITED);
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
                spiderURL.setStatus(SpiderURLStatus.ERROR);
            } finally {
                SPIDER_SERVICE.updateURLStatus(spiderURL);
            }
        }
    }

    private static class URLProducer extends Thread {
        public URLProducer(String originURL) {
            SPIDER_SERVICE.saveURL(SpiderURL.newSpiderURL(originURL, SpiderURLStatus.UNVISITED));
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    List<SpiderURL> unvisitedURLs = SPIDER_SERVICE.lookupUnvisitedURLs();
                    if (unvisitedURLs.isEmpty()) {
                        RUN_LOG.info("No-Unvisited-URL Found, and Spider is Stop...");
                        interruptLoop();
                    }

                    for (SpiderURL spiderURL : unvisitedURLs) {
                        Document document = Jsoup.connect(spiderURL.getUrl().trim()).timeout(5000).get();
                        document.select("a[href]").forEach(link -> {
                            SpiderURL spiderUrl = SpiderURL.newSpiderURL(link.attr("abs:href"), spiderURL.getUrl(), SpiderURLStatus.UNVISITED);
                            boolean saved = Spider.SPIDER_SERVICE.saveURL(spiderUrl);

                            if (saved) {
                                UNVISITED_URLS.offer(spiderURL);

                                // URL 产能过剩,小歇3分钟
                                if (UNVISITED_URLS.size() > DEFAULT_QUEUE_SIZE) {
                                    try {
                                        TimeUnit.MINUTES.sleep(3);
                                    } catch (Exception e) {
                                        ;
                                    }
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    RUN_LOG.error(e.getMessage(), e);
                }
            }
        }

        private void interruptLoop() {
            while (!UNVISITED_URLS.isEmpty()) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    RUN_LOG.warn(String.format("Customer-Thread Still Running [taskSize=%d]", UNVISITED_URLS.isEmpty()));
                } catch (Exception e) {
                    RUN_LOG.error(e.getMessage(), e);
                }
            }

            CUSTOMER_THREADS.forEach(customer -> {
                customer.interrupt();
                RUN_LOG.info(String.format("Customer-Thread-Stopped.[tid=%d]", customer.tid));
            });

            Thread.currentThread().interrupt();
            RUN_LOG.info("Prodcuer-Thread-Stopped.");
        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            ConfigHelper.load(args[0].trim());
        }

        // 注册文档处理器
        Spider.registerProcessor(new ImageProcessor());

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i ++) {
            URLConsumer customer = new URLConsumer(i);
            CUSTOMER_THREADS.add(customer);
            customer.start();
            RUN_LOG.info(String.format("Customer-Thread-Start.[tid=%d]", customer.tid));

        }

        // 启动URL处理器
        String originURL = ConfigHelper.containsKey("spider.source.url") ? ConfigHelper.get("spider.source.url") : DEFAULT_SOURCE_URL;
        new URLProducer(originURL).start();
    }

}
