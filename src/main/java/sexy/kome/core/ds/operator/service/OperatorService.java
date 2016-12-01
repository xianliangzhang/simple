package sexy.kome.core.ds.operator.service;

import sexy.kome.core.ds.operator.dao.OperatorMapper;
import sexy.kome.core.ds.operator.model.Operator;

/**
 * Created by Hack on 2016/12/1.
 */
public class OperatorService {

    private OperatorMapper operatorDao;

    public Operator find(Long id) {
        return operatorDao.find(id);
    }

    public void save(Operator operator) {
        operatorDao.save(operator);
    }
}
