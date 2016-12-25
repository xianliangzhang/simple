package sexy.kome.spider.model;

import java.util.Date;

/**
 * Created by Hack on 2016/12/23.
 */
public class FileUrl {
    public static final String TYPE_IMAGE = "IMAGE";

    private Long id;
    private String url;
    private String type;
    private Date createTime;

    public FileUrl() {

    }

    public FileUrl(String url) {
        this.url = url;
        this.type = TYPE_IMAGE;
        this.createTime = new Date();
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
