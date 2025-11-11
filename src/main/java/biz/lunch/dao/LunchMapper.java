package biz.lunch.dao;

import org.apache.ibatis.annotations.Param;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 점심/커피 정산 Mapper
 */
@Mapper("lunchMapper")
public interface LunchMapper {
    int registerLunch(Map<String, Object> params) throws Exception;

    int updateLunch(Map<String, Object> params) throws Exception;

    //수정 시 해당 내역의 참여자 목록 초기화
    int deleteParticipantsByLunchId(@Param("lunchId") int lunchId) throws Exception;

    // 수정된 참여자 정보를 일괄 등록
    int insertParticipantsBatch(Map<String, Object> params) throws Exception;

    List<Map<String, Object>> getLunchList(Map<String, Object> params) throws Exception;

    List<Map<String, Object>> getStatistics(Map<String, Object> params) throws Exception;

    int completeSettlement(Map<String, Object> params) throws Exception;

    int deleteLunch(@Param("lunchId") int lunchId);

    int updateSummaryAfterChange(Map<String, Object> params) throws Exception;

    String getLunchDateById(@Param("lunchId") int lunchId) throws Exception;

    List<Map<String, Object>> getUserList() throws Exception;

}
