package sexy.kome.core.ds.operator.dao;

import org.apache.ibatis.annotations.Select;
import sexy.kome.core.ds.operator.model.Operator;

/**
 * Created by Hack on 2016/12/1.
 */
public interface OperatorMapper {
    @Select("SELECT * FROM operator WHERE id = #{id}")
    public Operator find(Long id);
    public void save(Operator operator);
}
