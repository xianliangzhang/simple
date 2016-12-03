package sexy.kome.spider.model;

import java.util.Date;

/**
 * Created by Hack on 2016/12/2.
 */
public class SpiderFile {
    private Long id;
    private SpiderFileType type;
    private String fromDocUrl;
    private String md5;
    private String fileIdentifier;
    private Long size;
    private Date createTime;
    private Date updateTime;

    public static SpiderFile newSpiderFile(String md5) {
        SpiderFile spiderFile = new SpiderFile();
        spiderFile.setMd5(md5);
        return spiderFile;
    }

    public static SpiderFile newSpiderFile(SpiderFileType type, String fromDocRul, Long size, String identifier) {
        SpiderFile spiderFile = new SpiderFile();
        spiderFile.setType(type);
        spiderFile.setFromDocUrl(fromDocRul);
        spiderFile.setFileIdentifier(identifier);
        spiderFile.setMd5(identifier.substring(0, identifier.indexOf(".")));
        spiderFile.setSize(size);
        return spiderFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SpiderFileType getType() {
        return type;
    }

    public void setType(SpiderFileType type) {
        this.type = type;
    }

    public String getFromDocUrl() {
        return fromDocUrl;
    }

    public void setFromDocUrl(String fromDocUrl) {
        this.fromDocUrl = fromDocUrl;
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
