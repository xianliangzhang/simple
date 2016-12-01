package sexy.kome.core.ds.operator.dao;

import sexy.kome.core.ds.operator.model.Operator;

/**
 * Created by Hack on 2016/12/1.
 */
public interface OperatorMapper {
    Operator find(Long id);
    void save(Operator operator);
}
