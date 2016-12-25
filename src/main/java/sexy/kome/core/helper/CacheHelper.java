package sexy.kome.core.helper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import java.io.Reader;
import java.util.Properties;

/**
 * Created by Hack on 2016/12/2.
 */
public class CacheHelper {
    private static final Logger RUN_LOG = Logger.getLogger(CacheHelper.class);
    private static final SqlSessionFactory SQL_SESSION_FACTORY = getSqlSessionFactory(DBIdentifier.KOME);

    public enum DBIdentifier {
        KOME
    }

    private CacheHelper() {

    }

    private static SqlSessionFactory getSqlSessionFactory(DBIdentifier identifier) {
        try {
            Properties properties = new Properties();
            properties.setProperty("username", ConfigHelper.get("_env.KOME_X"));
            properties.setProperty("password", ConfigHelper.get("_env.KOME_Y"));
            Reader reader = Resources.getResourceAsReader("datasource/".concat(identifier.toString().toLowerCase()).concat(".xml"));
            return new SqlSessionFactoryBuilder().build(reader, properties);
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return SQL_SESSION_FACTORY;
    }
}
