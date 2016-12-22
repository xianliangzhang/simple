package sexy.kome.core.helper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hack on 2016/11/23.
 */
public class HdfsHelper {
    private static final Logger RUN_LOG = Logger.getLogger(HdfsHelper.class);
    private static final String HADOOP_CLUSTER = ConfigHelper.get("hadoop.cluster.namenode");
    private static final String HDFS_CLUSTER_PREFIX = "hdfs://".concat(HADOOP_CLUSTER).concat("/");
    private static final String HADOOP_USER = "root";
    private static final Configuration DEFAULT_CONFIGURATION = new Configuration();

    static {
        DEFAULT_CONFIGURATION.setBoolean("dfs.support.append", true);
    }

    public static FileSystem getFileSystem() {
        try {
            FileSystem fileSystem = FileSystem.get(URI.create(HDFS_CLUSTER_PREFIX), DEFAULT_CONFIGURATION, HADOOP_USER);
            return fileSystem;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static FileSystem getFileSystem(String uri) {
        try {
            FileSystem fileSystem = FileSystem.get(URI.create(uri), DEFAULT_CONFIGURATION, HADOOP_USER);
            return fileSystem;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static void write(String uri, String text) throws Exception {
        FileSystem fs = getFileSystem();
        FSDataOutputStream outputStream = null;
        if (!fs.exists(new Path(FILE_PATH))) {
            outputStream = fs.create(new Path(FILE_PATH));
            System.out.println("File Create ...");
        } else {
            outputStream = fs.append(new Path(FILE_PATH));
            System.out.println("File Append ...");
        }
        outputStream.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).concat( " xx yy z xx \n").getBytes());
        outputStream.hflush();
        org.apache.hadoop.io.IOUtils.closeStream(outputStream);
    }

    static final String FILE_PATH_DIR = HDFS_CLUSTER_PREFIX.concat("/wc/input");
    static final String FILE_PATH = FILE_PATH_DIR.concat("/fo.txt");

    static class DataLog {
        public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        public long tid;
        public String message;

        public DataLog(long tid, String message) {
            this.tid = tid;
            this.message = message;
        }

        public String toString() {
            return String.format("- %d %s %s", tid, DATE_FORMAT.format(new Date()), message);
        }
    }

    static boolean running = true;
    public static void main(String[] args) throws Exception {
        Thread writeThread = new Thread() {
            public void run() {
                while (running) {
                    try {
                        HdfsHelper.write(FILE_PATH, "GOID\n");
                    } catch (Exception e) {
                        System.out.print("E ------- ");
                        running = false;
                    }
                }
            }
        };

        try {
            writeThread.start();
            System.out.println(" <<<<<<<< ");

            TimeUnit.SECONDS.sleep(10);
            running = false;
            System.out.println(" >>>>>>>>>>>>> ");
        } catch (InterruptedException e) {
            ;
        }
    }
}
