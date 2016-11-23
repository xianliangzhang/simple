package com.willer;

import com.willer.common.ConfigHelper;
import com.willer.wc.WordCount;
import com.willer.weather.MaxTemperature;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Hello world!
 */
public class App {
    private static void testWordCount(String input, String output) throws Exception {
        WordCount.main("/Users/Hack/lab/input", "/Users/Hack/lab/output");
    }

    private static void testMaxTemperature(String input, String output) throws Exception {
        MaxTemperature.main("/Users/Hack/lab/input/", "/Users/Hack/lab/output");
    }

    public static void main(String[] args) throws Exception {
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        String hadoopCluster = ConfigHelper.get("hadoop.cluster.namenode");
        String hadoopURL = "hdfs://".concat(hadoopCluster).concat("/user/willer/input/weather.log");
        URL url = new URL(hadoopURL);

        InputStream in = url.openStream();
        OutputStream out = new FileOutputStream("/Users/Hack/lab/weather.copy.log");

        byte[] buffer = new byte[2048];
        int readSize = -1;
        while ((readSize = in.read(buffer)) != -1) {
            out.write(buffer, 0, readSize);
        }
        org.apache.commons.io.IOUtils.closeQuietly(in);
        System.out.println("OK");
    }
}
