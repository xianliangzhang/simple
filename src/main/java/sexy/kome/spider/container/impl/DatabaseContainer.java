package sexy.kome.spider.container.impl;

import sexy.kome.spider.container.Container;
import sexy.kome.spider.container.impl.mapper.SpiderURL;
import sexy.kome.spider.container.impl.mapper.SpiderURLMapper;

/**
 * Created by Hack on 2016/12/17.
 */
public class DatabaseContainer implements Container {
    private static final SpiderURLMapper SPIDER_URL_MAPPER = new SpiderURLMapper();

    @Override
    public void saveUnvisitedDocumentURL(String url) {
        SPIDER_URL_MAPPER.save(new SpiderURL(SpiderURL.Type.DOCUMENT_URL, SpiderURL.Status.UNVISITED, url));
    }

    @Override
    public String getUnvisitedDocumentURL() {
        return SPIDER_URL_MAPPER.lookupUnvisitedDocumentURL();
    }

    @Override
    public boolean hasVisitedImageURL(String url) {
        return !SPIDER_URL_MAPPER.isURLExists(url);
    }

    @Override
    public void saveVisitedImageURL(String url) {
        SPIDER_URL_MAPPER.save(new SpiderURL(SpiderURL.Type.IMAGE_URL, SpiderURL.Status.VISITED, url));
    }

    public static void main(String[] args)  throws Exception {
        String rs = new DatabaseContainer().getUnvisitedDocumentURL();
    }
}
