package sexy.kome.core.ds;

import com.willer.common.ConfigHelper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import sexy.kome.core.ds.operator.dao.OperatorMapper;
import sexy.kome.core.ds.operator.model.Operator;

import java.io.Reader;
import java.util.Properties;

/**
 * Created by Hack on 2016/12/1.
 */
public class KomeDataSource {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("username", ConfigHelper.get("_env.KOME_X"));
        properties.setProperty("password", ConfigHelper.get("_env.KOME_Y"));

        Reader reader = Resources.getResourceAsReader("mybatis.xml");

        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, properties);
        Operator operator = factory.openSession().getMapper(OperatorMapper.class).find(1L);
        System.out.println(operator.getId());
    }
}
