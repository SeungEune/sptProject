package biz.lunch.dao;

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

    int deleteLunch(int id) throws Exception;

    List<Map<String, Object>> getLunchList(Map<String, Object> params) throws Exception;

    Map<String, Object> getStatistics(Map<String, Object> params) throws Exception;

    int completeSettlement(Map<String, Object> params) throws Exception;
}
