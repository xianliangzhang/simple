package sexy.kome.spider;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
import sexy.kome.spider.container.Container;
import sexy.kome.spider.container.impl.MemoryCacheContainer;
import sexy.kome.spider.processer.Processor;
import sexy.kome.spider.processer.impl.ImageProcessor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hack on 2016/11/27.
 */
public class Spider {
    private static final Logger RUN_LOG = Logger.getLogger(Spider.class);
    private static final Set<Processor> PROCESSORS = new HashSet<Processor>();
    public static final int MAX_URL_LENGTH = 100;
    public static Container CONTAINER;

    private Spider(String originURL, Container container, Processor... processors) {
        CONTAINER = container;
        CONTAINER.saveUnvisitedDocumentURL(originURL);

        for (int i = 0; i < processors.length; i++) {
            PROCESSORS.add(processors[i]);
        }
    }

    private void start() {
        while (true) {
            try {
                Document document = Jsoup.connect(CONTAINER.getUnvisitedDocumentURL()).timeout(5000).get();
                document.select("a[href]").forEach(link -> {

                    PROCESSORS.forEach(processor -> {
                        processor.process(document);
                    });

                    String targetURL = link.attr("abs:href");
                    if (targetURL.length() <= MAX_URL_LENGTH) {
                        CONTAINER.saveUnvisitedDocumentURL(targetURL);
                    }
                });
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String originURL = args.length > 0 ? args[0] : ConfigHelper.get("spider.source.url");
        new Spider(originURL, new MemoryCacheContainer(), new ImageProcessor()).start();
    }

}
