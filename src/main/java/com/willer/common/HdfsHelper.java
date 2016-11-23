package com.willer.common;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.net.URI;

/**
 * Created by Hack on 2016/11/23.
 */
public class HdfsHelper {
    private static final Logger RUN_LOG = Logger.getLogger(HdfsHelper.class);
    private static final String HADOOP_CLUSTER = ConfigHelper.get("hadoop.cluster.namenode");
    private static final String HDFS_CLUSTER_PREFIX = "hdfs://".concat(HADOOP_CLUSTER).concat("/");
    private static final Configuration DEFAULT_CONFIGURATION = new Configuration();

    public static FileSystem getFileSystem() {
        try {
            FileSystem fileSystem = FileSystem.get(URI.create(HDFS_CLUSTER_PREFIX), DEFAULT_CONFIGURATION);
            return fileSystem;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static FileSystem getFileSystem(String user) {
        try {
            FileSystem fileSystem = FileSystem.get(URI.create(HDFS_CLUSTER_PREFIX), DEFAULT_CONFIGURATION, user);
            return fileSystem;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        FileSystem fileSystem = FileSystem.get(URI.create(HDFS_CLUSTER_PREFIX), new org.apache.hadoop.conf.Configuration(), "willer");
        fileSystem.exists(new Path("foo.txt"));
    }
}
