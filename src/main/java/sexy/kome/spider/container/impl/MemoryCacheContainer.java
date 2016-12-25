package sexy.kome.spider.container.impl;

import org.apache.log4j.Logger;
import sexy.kome.spider.container.Container;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hack on 2016/12/17.
 */
public class MemoryCacheContainer implements Container {
    private static final Logger RUN_LOG = Logger.getLogger(MemoryCacheContainer.class);
    private static final Set<String> URL_VISITED = new HashSet<String>();
    private static final BlockingQueue<String> URL_UNVISITED = new LinkedBlockingQueue<String>();
    private static final Set<String> URL_IMAGE_VISITED = new HashSet<String>();

    @Override
    public void saveUnvisitedDocumentURL(String url) {
        if (!URL_VISITED.contains(url) && !URL_UNVISITED.contains(url)) {
            URL_UNVISITED.offer(url);
            RUN_LOG.info(String.format("PUT-URL [UNVISITED=%d, VISITED=%d]", URL_UNVISITED.size(), URL_VISITED.size()));
        }
    }

    @Override
    public String getAndUpdateNextUnvisitedDocumentURL() {
        try {
            String url = URL_UNVISITED.take();
            URL_VISITED.add(url);
            RUN_LOG.info(String.format("GET-URL [UNVISITED=%d, VISITED=%d]]", URL_UNVISITED.size(), URL_VISITED.size()));
            return url;
        } catch (InterruptedException e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean hasVisitedImageURL(String url) {
        return URL_IMAGE_VISITED.contains(url);
    }

    @Override
    public void saveVisitedImageURL(String url) {
        URL_IMAGE_VISITED.add(url);
        RUN_LOG.info(String.format("PUT-IMAGE-URL [VISITED=%d]", URL_IMAGE_VISITED.size()));
    }


}
