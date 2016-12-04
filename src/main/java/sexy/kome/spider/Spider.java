package sexy.kome.spider;

import org.apache.log4j.Logger;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.model.SpiderURL;
import sexy.kome.spider.processer.Processor;
import sexy.kome.spider.processer.impl.ImageProcessor;
import sexy.kome.spider.service.SpiderService;

import java.util.HashSet;
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
    private static final Set<Class<? extends Processor>> DOCUMENT_PROCESSORS = new HashSet<Class<? extends Processor>>();
    private static final int DEFAULT_QUEUE_SIZE = 1000;
    private static final BlockingDeque<SpiderURL> UNVISITED_URLS = new LinkedBlockingDeque<SpiderURL>(DEFAULT_QUEUE_SIZE);
    private static final SpiderService SPIDER_SERVICE = new SpiderService();
    private static final URLProducer URL_PRODUCER_THREAD = new URLProducer();
    private static final Set<URLConsumer> URL_CUSTOMER_THREADS = new HashSet<URLConsumer>();

    private Spider() {

    }

    public static SpiderService getSpiderService() {
        return SPIDER_SERVICE;
    }

    public static SpiderURL getUnvisitedURL() throws Exception {
        return UNVISITED_URLS.take();
    }

    public static void putUnvisitedURL(SpiderURL spiderURL) {
        if (!UNVISITED_URLS.contains(spiderURL)) {
            while (UNVISITED_URLS.size() > DEFAULT_QUEUE_SIZE) {
                RUN_LOG.warn(String.format("UNVISITED-URLS Too Much [size=%d]", UNVISITED_URLS.size()));
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    RUN_LOG.error(e.getMessage(), e);
                }
            }
            UNVISITED_URLS.offer(spiderURL);
            if (UNVISITED_URLS.size() > 100) {
                RUN_LOG.warn(String.format("UNVISITED-URLS Size [size=%d]", UNVISITED_URLS.size()));
            }
        }
    }

    public static void registerProcessor(Class<? extends Processor> processor) {
        DOCUMENT_PROCESSORS.add(processor);
    }

    public static void start() {
        for (int i = 0; i < Runtime.getRuntime().availableProcessors()*2; i ++) {
            URLConsumer customer = new URLConsumer(i, DOCUMENT_PROCESSORS);
            URL_CUSTOMER_THREADS.add(customer);
            customer.start();
            RUN_LOG.info(String.format("Customer-Thread-Start.[tid=%d]", i));
        }

        URLConsumer customer = new URLConsumer(1, DOCUMENT_PROCESSORS);
        URL_CUSTOMER_THREADS.add(customer);
        customer.start();
        URL_PRODUCER_THREAD.start();
        RUN_LOG.info(String.format("Prodcuer-Thread-Start."));
    }

    public static void stop() {
        URL_PRODUCER_THREAD.interrupt();

        while (!UNVISITED_URLS.isEmpty()) {
            RUN_LOG.warn(String.format("Waiting-For-Consume-Unvisited-URLs [size=%d]", UNVISITED_URLS.size()));
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        }

        URL_CUSTOMER_THREADS.forEach(consumer -> {
            consumer.interrupt();
        });
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            ConfigHelper.load(args[0].trim());
        }

        // 注册文档处理器
        Spider.registerProcessor(ImageProcessor.class);

        // 爬呀爬,爬到外婆家
        Spider.start();
    }

}
