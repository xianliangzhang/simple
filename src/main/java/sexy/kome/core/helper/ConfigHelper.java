package sexy.kome.core.helper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * 1、默认读取 classpath:config.properties
 * 2、load 方法读取外部 properties 文件时，覆盖原有属性
 * 3、_env. 开头的key，如果属性文件未配置，则去系统环境变量查找
 */
public class ConfigHelper {
    private static final Logger RUN_LOG = Logger.getLogger(ConfigHelper.class);
    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();
    private static final String ENVIRONMENT_VARIABLE_PREFIX = "_env."; // 从环境变量取

    // 一次性加载所有配置文件中的信息
    static {
        loadFile("config.properties");
    }

    private static void loadFile(String filePath) {
        Reader reader = null;
        try {
            if (filePath.equals(DEFAULT_CONFIG_FILE)) {
                reader = new InputStreamReader(ConfigHelper.class.getClassLoader().getResourceAsStream(filePath), "UTF-8");
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
    private ConfigHelper() {
        throw new IllegalAccessError("ConfigUtil Cannot be Instanced!");
    }

    // 获取配置属性
    public static String get(String key) {
        String v = (String) properties.get(key);
        if (StringUtils.isEmpty(v) && key.startsWith(ENVIRONMENT_VARIABLE_PREFIX)) {
            v = System.getenv(key.substring(ENVIRONMENT_VARIABLE_PREFIX.length()));
        }
        return v;
    }

    // 判断属性key是否存在
    public static boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public static void main(String[] args) {
        System.out.println(ConfigHelper.get("test.key"));
        load("/Users/Hack/lab/config.properties");
        System.out.println(ConfigHelper.get("test.key"));
    }
}
