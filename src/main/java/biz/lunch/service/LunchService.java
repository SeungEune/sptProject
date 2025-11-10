package biz.lunch.service;

import java.util.List;
import java.util.Map;

/**
 * 점심/커피 정산 서비스 인터페이스
 */
public interface LunchService {
    //점심/커피 내역 등록
    int registerLunch(Map<String, Object> params) throws Exception;
    //점심/커피 내역 수정
    int updateLunch(Map<String, Object> params) throws Exception;
    //점심/커피 내역 삭제
    int deleteLunch(int id) throws Exception;
    //점심/커피 내역 목록 조회
    List<Map<String, Object>> getLunchList(Map<String, Object> params) throws Exception;
    //사용자별/월별 통계 조회
    Map<String, Object> getStatistics(Map<String, Object> params) throws Exception;
    //정산 완료 처리
    int completeSettlement(Map<String, Object> params) throws Exception;
}
