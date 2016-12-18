package sexy.kome.spider.container.impl;

import sexy.kome.spider.container.Container;

/**
 * Created by Hack on 2016/12/17.
 */
public class DatabaseContainer implements Container {

    @Override
    public void saveUnvisitedDocumentURL(String url) {
    }

    @Override
    public String getUnvisitedDocumentURL() {
        return null;
    }

    @Override
    public boolean hasVisitedImageURL(String url) {
        return false;
    }

    @Override
    public void saveVisitedImageURL(String url) {

    }
}
