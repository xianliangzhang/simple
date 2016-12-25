package sexy.kome.spider.container;

/**
 * Created by Hack on 2016/12/17.
 */
public interface Container  {
    // 保存一条未访问过的页面URL
    void saveUnvisitedDocumentURL(String url);

    // 获取一条未访问过的页面URL,并将其访问状态置为已访问
    String getAndUpdateNextUnvisitedDocumentURL();

    // 查看一条图片链接是否已经访问过
    boolean hasVisitedImageURL(String url);

    // 保存一条已经访问过的图片链接
    void saveVisitedImageURL(String url);

}
