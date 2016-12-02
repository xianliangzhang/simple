package sexy.kome.core.ds;

import com.willer.common.ConfigHelper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import java.io.Reader;
import java.util.Properties;

/**
 * Created by Hack on 2016/12/2.
 */
public class GlobalSqlSessionFactory {
    private static final Logger RUN_LOG = Logger.getLogger(GlobalSqlSessionFactory.class);
    private static final SqlSessionFactory sqlSessionFactory = getSessionFactory();

    private GlobalSqlSessionFactory() {

    }

    public static SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    private static SqlSessionFactory getSessionFactory() {
        try {
            if (null == sqlSessionFactory) {
                Properties properties = new Properties();
                properties.setProperty("username", ConfigHelper.get("_env.KOME_X"));
                properties.setProperty("password", ConfigHelper.get("_env.KOME_Y"));
                Reader reader = Resources.getResourceAsReader("mybatis.xml");
                return new SqlSessionFactoryBuilder().build(reader, properties);
            }

        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
        return sqlSessionFactory;
    }
}
