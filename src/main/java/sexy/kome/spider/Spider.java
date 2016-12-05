package sexy.kome.spider;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sexy.kome.core.helper.ConfigHelper;
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
    private static final BlockingQueue<String> URL_VISITED = new LinkedBlockingQueue<String>();
    private static final BlockingQueue<String> URL_UNVISITED = new LinkedBlockingQueue<String>();
    public static final int MAX_URL_LENGTH =100;

    private Spider(String originURL, Processor... processors) {
        URL_UNVISITED.offer(originURL);

        for (int i = 0; i < processors.length; i ++) {
            PROCESSORS.add(processors[i]);
        }
    }

    private void putURL(String url) {
        if (!URL_VISITED.contains(url) && !URL_UNVISITED.contains(url)) {
            URL_UNVISITED.offer(url);
            RUN_LOG.info(String.format("---- PUT-URL ---- UNVISITED/VISITED ---- %d/%d ----", URL_UNVISITED.size(), URL_VISITED.size()));
        }
    }

    private String getURL() {
        try {
            String url = URL_UNVISITED.take();
            URL_VISITED.add(url);
            RUN_LOG.info(String.format("---- PUT-GET ---- UNVISITED/VISITED ---- %d/%d ----\n", URL_UNVISITED.size(), URL_VISITED.size()));
            return url;
        } catch (InterruptedException e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private void start() {
        while (true) {
            try {
                Document document = Jsoup.connect(getURL()).timeout(5000).get();
                document.select("a[href]").forEach(link -> {

                    PROCESSORS.forEach(processor -> {
                        processor.process(document);
                    });

                    String targetURL = link.attr("abs:href");
                    if (targetURL.length() <= MAX_URL_LENGTH) {
                        putURL(targetURL);
                    }
                });
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String originURL = args.length > 0 ? args[0] : ConfigHelper.get("spider.source.url");
        new Spider(originURL, new ImageProcessor()).start();
    }

}
