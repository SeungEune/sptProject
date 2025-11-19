package biz.lunch.service;

import biz.lunch.vo.LunchVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;

import java.util.List;

/**
 * 점심/커피 정산 서비스 인터페이스
 */
public interface LunchService {
    // 사용자 목록 조회
    List<UserVO> getUserList() throws Exception;
    
    // 점심/커피 내역 등록
    int registerLunch(LunchVO lunchVO) throws Exception;
    
    // 점심/커피 내역 수정
    int updateLunch(LunchVO lunchVO) throws Exception;
    
    // 점심/커피 내역 삭제
    int deleteLunch(int lunchId) throws Exception;
    
    // 점심/커피 내역 목록 조회
    List<LunchVO> getLunchList(LunchVO searchVO) throws Exception;
    
    // 사용자별/월별 통계 조회
    List<SummaryVO> getStatistics(String searchMonth) throws Exception;
    
    // 정산 완료 처리
    int completeSettlement(String month, String userId, String action) throws Exception;
}
