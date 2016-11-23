package com.willer;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;

import java.net.URI;
import java.net.URL;

/**
 * Created by Hack on 2016/11/23.
 */
public class URLTest {
    public static void main(String[] args) throws Exception {
        URI uri = new URI("hdfs://god:12345@localhost:8000/user/willer/foo.txt?xx=yy#god=x");
        System.out.println(uri);

        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        URL url = new URL("hdfs://god:12345@localhost:8000/user/willer/foo.txt?xx=yy#god=x");
        System.out.println(url);
    }
}
