package com.willer.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Hack on 2016/11/22.
 */
public class HdfsTransferHelper {
    private static final String HADOOP_CLUSTER_URL = "hdfs://".concat(Configuration.get("hadoop.cluster.namenode"));

    public static void put(String src, String dest) {
        try {
            File srcFie = new File(src);
            if (!srcFie.exists()) {
                throw new RuntimeException(String.format("Source File [%s] Not Found!", src));
            }
            InputStream in = new URL(HADOOP_CLUSTER_URL.concat(src)).openStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static InputStream get(String src) throws Exception {
        return new URL(HADOOP_CLUSTER_URL.concat(src)).openStream();
    }

}
