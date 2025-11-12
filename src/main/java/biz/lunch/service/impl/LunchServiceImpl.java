package biz.lunch.service.impl;

import biz.lunch.dao.LunchMapper;
import biz.lunch.service.LunchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("lunchService")
public class LunchServiceImpl implements LunchService {

    @Resource(name = "lunchMapper")
    private LunchMapper lunchMapper;

    /**
     * 요약 테이블 갱신
     */
    private void updateSummary(String dateStr) throws Exception {
        if (dateStr == null || dateStr.isEmpty()) {
            log.warn("date 파라미터가 null이거나 비어 있습니다. 요약 갱신을 건너뜁니다.");
            return;
        }

        String month = dateStr.substring(0, 7);
        log.info("요약 테이블 갱신 시작 (month={})", month);

        Map<String, Object> map = new HashMap<>();
        map.put("month", month);
        lunchMapper.updateSummaryAfterChange(map);
    }

    /**
     * 점심/커피 내역 등록
     */

    @Override
    public List<Map<String, Object>> getUserList() throws Exception {
        return lunchMapper.getUserList();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int registerLunch(Map<String, Object> params) throws Exception {
        log.info("점심/커피 내역 등록 - params={}", params);

        int totalAmount = 0;
        if (params.containsKey("participants")) {
            List<Map<String, Object>> participants = (List<Map<String, Object>>) params.get("participants");
            if (participants != null) {
                for (Map<String, Object> p : participants) {
                    // 컨트롤러에서 문자열로 넘어왔으므로 숫자로 변환
                    totalAmount += Integer.parseInt(p.get("individualAmount").toString());
                }
            }
        }
        params.put("totalAmount", totalAmount);

        int masterResult = lunchMapper.registerLunch(params);
        if (masterResult > 0 && params.containsKey("lunchId")) {

            if (params.containsKey("participants")) {
                lunchMapper.insertParticipantsBatch(params);
                log.debug("참여자 등록 완료");
            }

            // 등록된 날짜 기준으로 summary 갱신
            updateSummary((String) params.get("date"));
        } else {
            throw new Exception("lunch_master 등록 실패");
        }

        return masterResult;
    }

    /**
     * 점심/커피 내역 수정
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLunch(Map<String, Object> params) throws Exception {
        log.info("점심/커피 내역 수정 - params={}", params);
        int totalAmount = 0;
        if (params.containsKey("participants")) {
            List<Map<String, Object>> participants = (List<Map<String, Object>>) params.get("participants");
            if (participants != null) {
                for (Map<String, Object> p : participants) {
                    // 컨트롤러에서 문자열로 넘어왔으므로 숫자로 변환
                    totalAmount += Integer.parseInt(p.get("individualAmount").toString());
                }
            }
        }
        params.put("totalAmount", totalAmount);
        int result = lunchMapper.updateLunch(params);
        int lunchId = Integer.parseInt(params.get("lunchId").toString());

        lunchMapper.deleteParticipantsByLunchId(lunchId);
        if (params.containsKey("participants")) {
            lunchMapper.insertParticipantsBatch(params);
        }

        // 수정된 날짜 기준으로 summary 갱신
        updateSummary((String) params.get("date"));

        log.info("점심/커피 내역 수정 완료 (lunchId={})", lunchId);
        return result;
    }

    /**
     * 점심/커피 내역 삭제
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLunch(int lunchId) throws Exception {
        log.info("점심/커피 내역 삭제 요청 - lunchId={}", lunchId);

        if (lunchId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 lunchId입니다.");
        }

        // 삭제 전 날짜 조회 → 해당 월 기준으로 summary 갱신
        String dateStr = lunchMapper.getLunchDateById(lunchId);

        int delMaster = lunchMapper.deleteLunch(lunchId);
        if (delMaster > 0) {
            updateSummary(dateStr);
            log.info("점심/커피 내역 삭제 완료 (lunchId={})", lunchId);
        } else {
            log.warn("삭제 대상이 존재하지 않음 (lunchId={})", lunchId);
        }

        return delMaster;
    }

    /**
     * 내역 목록 조회 (단순히 DB에서만 조회)
     */
    @Override
    public List<Map<String, Object>> getLunchList(Map<String, Object> params) throws Exception {
        log.info("점심/커피 목록 조회 - params={}", params);

        if (params.containsKey("searchMonth") && params.get("searchMonth") != null) {
            String monthStr = params.get("searchMonth").toString();
            if (monthStr.length() == 7) {
                YearMonth yearMonth = YearMonth.parse(monthStr);
                params.put("startDate", yearMonth.atDay(1).toString());
                params.put("endDate", yearMonth.atEndOfMonth().toString());
            }
        }
        return lunchMapper.getLunchList(params);
    }

    /**
     * 통계 요약 조회
     */
    @Override
    public List<Map<String, Object>> getStatistics(Map<String, Object> params) throws Exception {
        log.info("점심/커피 통계 조회 - params={}", params);

        if (params.containsKey("searchMonth") && !params.containsKey("month")) {
            params.put("month", params.get("searchMonth"));
        }

        return lunchMapper.getStatistics(params);
    }

    /**
     * 정산 완료 처리
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeSettlement(Map<String, Object> params) throws Exception {
        if (!params.containsKey("month") || !params.containsKey("userId")) {
            throw new IllegalArgumentException("month 또는 userId 누락");
        }
        String action = (String) params.getOrDefault("action", "complete");
        log.info("정산 처리 ({}): params={}", action, params);

        // Mapper가 'action' 파라미터를 보고 동적으로 쿼리 실행
        int result = lunchMapper.completeSettlement(params);

        if (result == 0) {
            log.warn("정산 처리 대상 없음 ({}): (month={}, userId={})",
                    action, params.get("month"), params.get("userId"));
        } else {
            log.info("정산 처리 성공 ({}): (month={}, userId={})",
                    action, params.get("month"), params.get("userId"));
        }
        return result;
    }
}
