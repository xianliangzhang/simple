package sexy.kome.spider;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sexy.kome.spider.model.SpiderURL;
import sexy.kome.spider.model.SpiderURLStatus;
import sexy.kome.spider.processer.Processor;
import sexy.kome.spider.processer.impl.ImageProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Hack on 2016/12/4.
 */
public class URLConsumer extends Thread {
    private static final Logger RUN_LOG = Logger.getLogger(URLConsumer.class);
    private int tid;
    private boolean running = true;
    private Set<Processor> URL_PROCESSORS = new HashSet<Processor>();

    public URLConsumer(int tid, Set<Class<? extends Processor>> processorClasses) {
        this.tid = tid;
        registerProcessors(processorClasses);
    }

    @Override
    public void run() {
        while (running) {
            try {
                process(Spider.getUnvisitedURL());
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
            URL_PROCESSORS.forEach(processor -> {
                processor.process(document);
            });

            spiderURL.setStatus(SpiderURLStatus.VISITED);
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
            spiderURL.setStatus(SpiderURLStatus.ERROR);
        } finally {
            Spider.getSpiderService().updateURLStatus(spiderURL);
        }
    }

    private void registerProcessors(Set<Class<? extends Processor>> processorClasses) {
        processorClasses.forEach(processor -> {
            try {
                URL_PROCESSORS.add(processor.newInstance());
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        });
    }

    public static void main(String... args) {
        Spider.registerProcessor(ImageProcessor.class);
        Set<Class<? extends Processor>> processors = new HashSet<Class<? extends Processor>>(){{
            add(ImageProcessor.class);
        }};

        SpiderURL spiderURL = Spider.getSpiderService().lookupUnvisitedURLs().get(0);
        Spider.putUnvisitedURL(spiderURL);

        URLConsumer customer = new URLConsumer(1, processors);
        customer.start();
    }

}
