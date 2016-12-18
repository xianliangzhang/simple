package sexy.kome.core.helper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hack on 2016/12/2.
 */
public class CacheHelper {
    private static final Logger RUN_LOG = Logger.getLogger(CacheHelper.class);
    private static final Map<DBIdentifier, SqlSessionFactory> SQL_SESSION_FACTORY_MAP = new ConcurrentHashMap<DBIdentifier, SqlSessionFactory>();

    public enum DBIdentifier {
        KOME, SPIDER
    }

    private CacheHelper() {

    }

    public static SqlSessionFactory getSqlSessionFactory(DBIdentifier identifier) {
        if (!SQL_SESSION_FACTORY_MAP.containsKey(identifier)) {
            try {
                Properties properties = new Properties();
                properties.setProperty("username", ConfigHelper.get("_env.KOME_X"));
                properties.setProperty("password", ConfigHelper.get("_env.KOME_Y"));
                Reader reader = Resources.getResourceAsReader("datasource/".concat(identifier.toString().toLowerCase()).concat(".xml"));
                SQL_SESSION_FACTORY_MAP.put(identifier, new SqlSessionFactoryBuilder().build(reader, properties));
            } catch (Exception e) {
                RUN_LOG.error(e.getMessage(), e);
            }
        }
        return SQL_SESSION_FACTORY_MAP.get(identifier);
    }

    public static Connection newConnection(DBIdentifier identifier) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://kome.sexy:3306/kome", ConfigHelper.get("_env.KOME_X"), ConfigHelper.get("_env.KOME_Y"));
            return connection;
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

}
