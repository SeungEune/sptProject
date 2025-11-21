package biz.eqp_history.service;

import biz.eqp_history.vo.EqpHistoryResponseVO;
import biz.eqp_history.vo.EqpHistoryVO;

import java.util.List;

public interface EqpHistoryService {
    void insertEqpHistoryVO(EqpHistoryVO eqpHistoryVO);
    List<EqpHistoryResponseVO> getEqpHistoryByEqpId(Long eqpId);
}
