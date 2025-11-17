package biz.enter.service;

import biz.enter.vo.EnterVO;

import java.util.List;

public interface EnterService {
    List<EnterVO> getEnterList();
    void createEnter(EnterVO enter);
    EnterVO getEnter(Long enterId) throws Exception;
    void updateEnter(EnterVO vo) throws Exception;
    void deleteEnter(Long enterId) throws Exception;
}
