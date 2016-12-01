package sexy.kome.core.ds;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import sexy.kome.core.ds.operator.dao.OperatorMapper;
import sexy.kome.core.ds.operator.model.Operator;

import java.sql.ResultSet;

/**
 * Created by Hack on 2016/11/30.
 */
public class DataSource {

    public static void main(String[] args) throws Exception {

        String user="guest";
        String pass="guest";

        javax.sql.DataSource dataSource = new BasicDataSource();
        ((BasicDataSource) dataSource).setDriverClassName("com.mysql.jdbc.Driver");
        ((BasicDataSource) dataSource).setUrl("jdbc:mysql://kome.sexy:8306/kome");
        ((BasicDataSource) dataSource).setUsername(user);
        ((BasicDataSource) dataSource).setPassword(pass);

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(OperatorMapper.class);

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        SqlSession session = sqlSessionFactory.openSession();

        OperatorMapper operatorMapper = session.getMapper(OperatorMapper.class);
        Operator operator = operatorMapper.find(1L);
        System.out.println(operator.getId() + " - " + operator.getName() + " - " + operator.getEmail());
    }
}
