package sexy.kome.spider.container.impl.mapper;

import java.util.Date;

/**
 * Created by Hack on 2016/12/18.
 */
public class SpiderURL {
    public enum Type {DOCUMENT_URL,IMAGE_URL,VIDEO_URL}
    public enum Status {UNVISITED, VISITED, ERROR}

    private Long id;
    private String url;
    private String type;
    private String status;
    private Date createTime;
    private Date updateTime;

    public SpiderURL() {

    }

    public SpiderURL(Type type, Status status, String url) {
        this.url = url;
        this.type = type.toString();
        this.status = status.toString();
        this.createTime = new Date();
        this.updateTime = new Date();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
