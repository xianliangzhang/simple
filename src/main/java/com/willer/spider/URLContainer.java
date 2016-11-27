package com.willer.spider;

import org.apache.hadoop.io.MD5Hash;
import org.apache.log4j.Logger;
import sun.security.provider.MD5;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hack on 2016/11/27.
 */
public class URLContainer {
    private static final Logger RUN_LOG = Logger.getLogger(URLContainer.class);
    private static final Queue<String> UNVISITED_URLS_QUEUE = new LinkedBlockingQueue<>();
    private static final Queue<String> VISITED_URLS_QUEUE = new LinkedBlockingQueue<String>();

    private URLContainer() {

    }

    public static void put(String url) {
        if (!UNVISITED_URLS_QUEUE.contains(url.trim()) && !VISITED_URLS_QUEUE.contains(url.trim())) {
            UNVISITED_URLS_QUEUE.offer(url.trim());
            RUN_LOG.info(String.format("Put-Url [url=%s]", url));
        }
    }

    public static String get() {
        try {
            String url = ((BlockingQueue<String>) UNVISITED_URLS_QUEUE).take();
            VISITED_URLS_QUEUE.add(url);
            return url;
        } catch (InterruptedException e) {
            RUN_LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
