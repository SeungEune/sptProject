package biz.enter.service;

import biz.enter.vo.EnterVO;

import java.util.List;

public interface EnterService {
    // Service
    public List<EnterVO> getEnterList(int page, int size);
    public int getEnterCount();
    void createEnter(EnterVO enter);
    EnterVO getEnter(String enterId);
    void updateEnter(EnterVO vo);
    void deleteEnter(String enterId);
}
