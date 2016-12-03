package sexy.kome.spider.model;

import java.util.Date;

/**
 * Created by Hack on 2016/12/2.
 */
public class SpiderURL {
    private Long id;
    private String url;
    private String fromDocUrl;
    private SpiderURLStatus status;
    private Date createTime;
    private Date updateTime;

    public static SpiderURL newSpiderURL(String url) {
        SpiderURL spiderURL = new SpiderURL();
        spiderURL.setUrl(url);
        return spiderURL;
    }

    public static SpiderURL newSpiderURL(SpiderURLStatus status) {
        SpiderURL spiderURL = new SpiderURL();
        spiderURL.setStatus(status);
        return spiderURL;
    }

    public static SpiderURL newSpiderURL(String url, SpiderURLStatus status) {
        SpiderURL spiderURL = newSpiderURL(url);
        spiderURL.setStatus(status);
        return spiderURL;
    }

    public static SpiderURL newSpiderURL(String url, String fromDocUrl, SpiderURLStatus status) {
        SpiderURL spiderURL = newSpiderURL(url);
        spiderURL.setStatus(status);
        spiderURL.setFromDocUrl(fromDocUrl);
        return spiderURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFromDocUrl() {
        return fromDocUrl;
    }

    public void setFromDocUrl(String fromDocUrl) {
        this.fromDocUrl = fromDocUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public SpiderURLStatus getStatus() {
        return status;
    }

    public void setStatus(SpiderURLStatus status) {
        this.status = status;
    }
}
