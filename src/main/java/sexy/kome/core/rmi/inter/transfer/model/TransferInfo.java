package sexy.kome.core.rmi.inter.transfer.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by Hack on 2016/11/24.
 */
public class TransferInfo implements Serializable {
    private String name;
    private String path;
    private long size;
    private String md5;
    private InputStream inputStream;

    private TransferInfo() {

    }

    public static TransferInfo build(String path) {
        TransferInfo info = new TransferInfo();
        try {
            File file = new File(path);
            info.name = file.getName();
            info.size = file.length();
            info.path = path;
            info.inputStream = new FileInputStream(path);
        } catch (Exception e) {
            return null;
        }
        return info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
