package sexy.kome.spider.model;

import javax.print.Doc;
import java.util.Date;

/**
 * Created by Hack on 2016/12/23.
 */
public class DocumentUrl {
    public static final String STATUS_UNVISITED = "UNVISITED";
    public static final String STATUS_VISITED = "VISITED";
    public static final String STATUS_ERROR = "ERROR";

    private Long id;
    private String url;
    private String status;
    private Date createTime;
    private Date updateTime;

    public DocumentUrl() {

    }

    public DocumentUrl(String url) {
        this.url = url;
        this.status = STATUS_UNVISITED;
        this.createTime = new Date();
    }

    public void success() {
        this.status = STATUS_VISITED;
        this.updateTime = new Date();
    }

    public void failure() {
        this.status = STATUS_ERROR;
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
