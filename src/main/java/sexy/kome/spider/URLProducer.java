package sexy.kome.spider;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sexy.kome.spider.model.SpiderURL;
import sexy.kome.spider.model.SpiderURLStatus;

import java.util.List;

/**
 * Created by Hack on 2016/12/4.
 */
public class URLProducer extends Thread {
    private static final Logger RUN_LOG = Logger.getLogger(URLProducer.class);

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<SpiderURL> unvisitedURLs = Spider.getSpiderService().lookupUnvisitedURLs();
                RUN_LOG.info(String.format("Load-Unvisited-URLs From DB [size=%d]", unvisitedURLs.size()));
                if (unvisitedURLs.isEmpty()) {
                    RUN_LOG.info("No-Unvisited-URL Found, and Spider is Stop...");

                    // 所有URL都已经访问过了,可以停止爬虫了
                    Spider.stop();
                }

                for (SpiderURL spiderURL : unvisitedURLs) {
                    Document document = Jsoup.connect(spiderURL.getUrl().trim()).timeout(5000).get();
                    document.select("a[href]").forEach(link -> {
                        String targetURL = link.attr("abs:href");
                        SpiderURL oldSpiderURL = Spider.getSpiderService().lookupSpiderURLByURL(targetURL);

                        if (oldSpiderURL == null) {
                            SpiderURL newSpiderURL = SpiderURL.newSpiderURL(targetURL, spiderURL.getUrl(), SpiderURLStatus.UNVISITED);
                            Spider.getSpiderService().saveURL(newSpiderURL);
                            Spider.putUnvisitedURL(newSpiderURL);
                        } else {
                            Spider.putUnvisitedURL(oldSpiderURL);
                        }
                    });
                }
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        }
    }
}
