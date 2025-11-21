package biz.eqp_history.dao;

import biz.eqp_history.vo.EqpHistoryResponseVO;
import biz.eqp_history.vo.EqpHistoryVO;
import biz.mapper.biz.eqp_history.EqpHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository("eqpHistoryDAO")
@RequiredArgsConstructor
public class EqpHistoryDAO {

    @Resource(name="eqpHistoryMapper")
    private final EqpHistoryMapper eqpHistoryMapper;

    public void insertEqpHistory(EqpHistoryVO eqpHistory){eqpHistoryMapper.insertEqpHistory(eqpHistory);}

    public List<EqpHistoryResponseVO> getEqpHistoryByEqpId(Long eqpId) {return eqpHistoryMapper.getEqpHistoryByEqpId(eqpId);}

    public List<EqpHistoryVO> getAllEqpHistory(){return eqpHistoryMapper.getAllEqpHistory();}

    public void deleteById(Long id){eqpHistoryMapper.deleteById(id);}

}
