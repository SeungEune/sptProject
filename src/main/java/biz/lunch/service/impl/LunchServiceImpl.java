package biz.lunch.service.impl;

import biz.lunch.dao.LunchMapper;
import biz.lunch.service.LunchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("lunchService")
public class LunchServiceImpl implements LunchService {

    @Resource(name = "lunchMapper")
    private LunchMapper lunchMapper;

    @Override
    public int registerLunch(Map<String, Object> params) throws Exception {
        // TODO: 등록 로직
        return 0;
    }

    @Override
    public int updateLunch(Map<String, Object> params) throws Exception {
        // TODO: 수정 로직
        return 0;
    }

    @Override
    public int deleteLunch(int id) throws Exception {
        // TODO: 삭제 로직
        return 0;
    }

    @Override
    public List<Map<String, Object>> getLunchList(Map<String, Object> params) throws Exception {
        // TODO: 조회 로직
        return null;
    }

    @Override
    public Map<String, Object> getStatistics(Map<String, Object> params) throws Exception {
        // TODO: 통계 로직
        return null;
    }

    @Override
    public int completeSettlement(Map<String, Object> params) throws Exception {
        // TODO: 정산 완료 처리
        return 0;
    }
}
