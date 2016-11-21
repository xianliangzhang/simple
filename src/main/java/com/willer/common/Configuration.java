package com.willer.common;

import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * Created by Hack on 2016/11/21.
 */
public class Configuration {
    private static final Logger RUN_LOG = Logger.getLogger(Configuration.class);
    private static final Properties properties = new Properties();

    // 一次性加载所有配置文件中的信息
    static {
        try (Reader reader = new InputStreamReader(Configuration.class.getClassLoader().getResourceAsStream("config.properties"), "UTF-8")) {
            properties.load(reader);
            RUN_LOG.info("Load Properties File [config.properties]");
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
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
    }
}
