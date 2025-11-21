package biz.mapper.biz.eqp_history;

import biz.eqp_history.vo.EqpHistoryResponseVO;
import biz.eqp_history.vo.EqpHistoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EqpHistoryMapper {

    void insertEqpHistory(EqpHistoryVO eqpHistory);

    List<EqpHistoryResponseVO> getEqpHistoryByEqpId(Long eqpId);

    List<EqpHistoryVO> getAllEqpHistory();

    void deleteById(Long id);

}
