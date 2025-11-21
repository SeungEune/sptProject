package biz.eqp_history.service;

import biz.eqp_history.dao.EqpHistoryDAO;
import biz.eqp_history.vo.EqpHistoryResponseVO;
import biz.eqp_history.vo.EqpHistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@RequiredArgsConstructor
@Service
public class EqpHistoryServiceImpl implements EqpHistoryService{

    @Resource(name="eqpHistoryDAO")
    private final EqpHistoryDAO eqpHistoryDAO;

    @Override
    public void insertEqpHistoryVO(EqpHistoryVO eqpHistoryVO) {
        eqpHistoryDAO.insertEqpHistory(eqpHistoryVO);
    }

    @Override
    public List<EqpHistoryResponseVO> getEqpHistoryByEqpId(Long eqpId) {
        return eqpHistoryDAO.getEqpHistoryByEqpId(eqpId);
    }


}
