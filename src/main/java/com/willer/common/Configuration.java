package com.willer.common;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Created by Hack on 2016/11/21.
 */
public class Configuration {
    private static final Logger RUN_LOG = Logger.getLogger(Configuration.class);
    private static final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG_FILE = "config.properties";

    // 一次性加载所有配置文件中的信息
    static {
        loadFile("config.properties");
    }

    private static void loadFile(String filePath) {
        Reader reader = null;
        try {
            if (filePath.equals(DEFAULT_CONFIG_FILE)) {
                reader = new InputStreamReader(Configuration.class.getClassLoader().getResourceAsStream(filePath), "UTF-8");
                RUN_LOG.info("Load Default Configuration Properties [config.properties]");
            } else {
                reader = new InputStreamReader(new FileInputStream(filePath));
                RUN_LOG.info(String.format("Load Outer Configuration Properties [%s]", filePath));
            }
            properties.load(reader);
        } catch (IOException e) {
            RUN_LOG.error(e.getMessage(), e);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(reader);
        }
    }

    // 加载指定文件,以使优先级更高的属性覆盖优先级低的属性
    public static void load(String file) {
        File propertiesFile = new File(file);
        if (!propertiesFile.exists()) {
            RUN_LOG.warn(String.format("Properties File [%s] Not Found!", file));
        }

        loadFile(file);
    }

    // 禁止实例化该类
    private Configuration() {
        throw new IllegalAccessError("ConfigUtil Cannot be Instanced!");
    }

    // 获取配置属性
    public static String get(String key) {
        return (String) properties.get(key);
    }

    public static void main(String[] args) {
        System.out.println(Configuration.get("test.key"));
        load("/Users/Hack/lab/config.properties");
        System.out.println(Configuration.get("test.key"));
    }
}
