package sexy.kome.core.ds.operator.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import sexy.kome.core.ds.operator.dao.OperatorMapper;
import sexy.kome.core.ds.operator.model.Operator;
import sexy.kome.core.helper.CacheHelper;

/**
 * Created by Hack on 2016/12/1.
 */
public class OperatorService {
    private static final Logger RUN_LOG = Logger.getLogger(OperatorService.class);
    private static final SqlSessionFactory SQL_SESSION_FACTORY = CacheHelper.getSqlSessionFactory(CacheHelper.DBIdentifier.KOME);

    public Operator find(Long id) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession()) {
            return session.getMapper(OperatorMapper.class).find(id);
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public void save(Operator operator) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession()) {
            OperatorMapper mapper = session.getMapper(OperatorMapper.class);
            mapper.save(operator);
            session.commit();
        } catch (Exception e) {
            RUN_LOG.error(e.getMessage(), e);
        }
    }

    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0;i < length; i ++) {
            sb.append('a' + (int) (Math.random()*26));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        OperatorService operatorService = new OperatorService();
        for (int i = 0; i < 1000; i ++) {
            Operator operator = new Operator();
            operator.setName(randomString(20));
            operator.setEmail(randomString(20).concat("@qq.com"));
            operatorService.save(operator);
            System.out.println(String.format("Operator-Saved [id=%d, name=%s, email=%s]", operator.getId(), operator.getName(), operator.getEmail()));

            Operator o = operatorService.find(operator.getId());
            System.out.println(String.format("Operator-Found [id=%d, name=%s, email=%s]", o.getId(), o.getName(), o.getEmail()));
        }
    }
}
