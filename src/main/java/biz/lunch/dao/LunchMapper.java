package biz.lunch.dao;

import biz.lunch.vo.LunchVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import java.util.List;

/**
 * 점심/커피 정산 Mapper
 */
@Mapper("lunchMapper")
public interface LunchMapper {
    // 점심/커피 내역 등록
    int registerLunch(LunchVO lunchVO) throws Exception;

    // 점심/커피 내역 수정
    int updateLunch(LunchVO lunchVO) throws Exception;

    // 수정 시 해당 내역의 참여자 목록 초기화
    int deleteParticipantsByLunchId(@Param("lunchId") int lunchId) throws Exception;

    // 수정된 참여자 정보를 일괄 등록
    int insertParticipantsBatch(LunchVO lunchVO) throws Exception;

    // 점심/커피 내역 목록 조회
    List<LunchVO> getLunchList(LunchVO searchVO) throws Exception;

    // 사용자별/월별 통계 조회
    List<SummaryVO> getStatistics(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("month") String month) throws Exception;

    // 정산 완료 처리
    int completeSettlement(@Param("month") String month, @Param("userId") String userId, @Param("action") String action) throws Exception;

    // 점심/커피 내역 삭제
    int deleteLunch(@Param("lunchId") int lunchId);

    // 요약 테이블 자동 갱신
    int updateSummaryAfterChange(@Param("month") String month) throws Exception;

    // lunchId로 날짜 조회
    String getLunchDateById(@Param("lunchId") int lunchId) throws Exception;

    // 사용자 목록 조회
    List<UserVO> getUserList() throws Exception;
}
