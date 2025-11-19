package biz.enter.service;

import biz.enter.vo.EnterVO;

import java.util.List;

public interface EnterService {
    // Service
    public List<EnterVO> getEnterList(int page, int size) throws Exception;
    public int getEnterCount() throws Exception;
    void createEnter(EnterVO enter);
    EnterVO getEnter(String enterId) throws Exception;
    void updateEnter(EnterVO vo) throws Exception;
    void deleteEnter(String enterId) throws Exception;
}
