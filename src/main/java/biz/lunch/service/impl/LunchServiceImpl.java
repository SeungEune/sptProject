package biz.lunch.service.impl;

import biz.lunch.dao.LunchMapper;
import biz.lunch.service.LunchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service("lunchService")
public class LunchServiceImpl implements LunchService {

    @Resource(name = "lunchMapper")
    private LunchMapper lunchMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int registerLunch(Map<String, Object> params) throws Exception {
        log.info("점심/커피 내역 등록 - params={}", params);

        // 1. lunch_master 등록
        int masterResult = lunchMapper.registerLunch(params);

        if (masterResult > 0 && params.containsKey("lunchId")) {
            // 3. lunch_participant 등록
            if (params.containsKey("participants")) {
                List<Map<String, Object>> participants = (List<Map<String, Object>>) params.get("participants");

                if (participants != null && !participants.isEmpty()) {
                    lunchMapper.insertParticipantsBatch(params);
                    log.debug("lunch_participant 등록 완료 ({}명)", participants.size());
                }
            }
        } else {
            log.error("lunch_master 등록 실패. 롤백 처리합니다.");
            throw new Exception("lunch_master 등록에 실패했습니다.");
        }

        log.info("등록 완료");
        return masterResult;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLunch(Map<String, Object> params) throws Exception {
        log.info("점심/커피 내역 수정 - params={}", params);

        // lunch_master 테이블 수정
        int result = lunchMapper.updateLunch(params);
        log.debug("lunch_master 수정 결과: {}", result);

        Object lunchIdObj = params.get("lunchId");
        if (lunchIdObj == null) {
            throw new IllegalArgumentException("lunchId가 누락되었거나 null입니다.");
        }
        int lunchId = Integer.parseInt(lunchIdObj.toString());

        // 3. 기존 참여자 전체 삭제
        int delCount = lunchMapper.deleteParticipantsByLunchId(lunchId);
        log.debug("기존 참여자 삭제 완료 ({}건)", delCount);

        // 4. 새로운 참여자 목록 등록
        if (params.containsKey("participants")) {
            List<Map<String, Object>> participants = (List<Map<String, Object>>) params.get("participants");

            if (participants != null && !participants.isEmpty()) {
                int inserted = lunchMapper.insertParticipantsBatch(params);
                log.debug("신규 참여자 등록 완료 ({}건)", inserted);
            } else {
                log.info("새 참여자 정보가 없습니다.");
            }
        }

        log.info("점심/커피 내역 수정 완료 (lunch_id={})", lunchId);
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLunch(int lunchId) throws Exception {
        log.info("점심/커피 내역 삭제 요청 - lunch_id={}", lunchId);

        if (lunchId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 lunch_id입니다: " + lunchId);
        }
        // 참여자 삭제는 DB가 자동 처리 (CASCADE)
        int delMaster = lunchMapper.deleteLunch(lunchId);
        log.debug("lunch_master 삭제 완료 ({}건)", delMaster);

        if (delMaster == 0) {
            log.warn("삭제 대상이 존재하지 않거나 이미 삭제됨 (lunch_id={})", lunchId);
        } else {
            log.info("점심/커피 내역 삭제 완료 (lunch_id={})", lunchId);
        }

        return delMaster;
    }



    @Override
    public Map<String, Object> getLunchList(Map<String, Object> params) throws Exception {
        log.info("점심/커피 내역 및 자동 정산 계산 - params={}", params);

        // 기본 점심/커피 내역 조회
        List<Map<String, Object>> rawList = lunchMapper.getLunchList(params);
        if (rawList == null || rawList.isEmpty()) {
            log.info("조회 결과 없음");
            return Map.of("rawList", List.of(), "settlementList", List.of());
        }

        // 사용자별 합계 계산용 Map
        Map<String, Integer> totalPaid = new HashMap<>(); // 실제 낸 금액
        Map<String, Integer> totalOwed = new HashMap<>(); // 각자 부담금

        // 각 내역 순회하며 누적합 계산
        for (Map<String, Object> row : rawList) {
            String payerId = String.valueOf(row.get("payer_id"));
            int totalAmount = ((Number) row.get("total_amount")).intValue();

            // 결제자가 낸 금액 합산
            totalPaid.put(payerId, totalPaid.getOrDefault(payerId, 0) + totalAmount);

            // 참여자 문자열
            String participants = (String) row.get("participants");
            if (participants != null && !participants.isEmpty()) {
                String[] arr = participants.split(",");
                for (String p : arr) {
                    String[] kv = p.trim().split(":");
                    if (kv.length == 2) {
                        String userId = kv[0].trim();
                        int amount = Integer.parseInt(kv[1].trim());
                        totalOwed.put(userId, totalOwed.getOrDefault(userId, 0) + amount);
                    }
                }
            }
        }

        // 자동 정산 요약 (balance = 낸 금액 - 부담금)
        List<Map<String, Object>> settlementList = new ArrayList<>();
        Set<String> allUsers = new HashSet<>();
        allUsers.addAll(totalPaid.keySet());
        allUsers.addAll(totalOwed.keySet());

        for (String userId : allUsers) {
            int paid = totalPaid.getOrDefault(userId, 0);
            int owed = totalOwed.getOrDefault(userId, 0);
            int balance = paid - owed;

            Map<String, Object> s = new HashMap<>();
            s.put("user_id", userId);
            s.put("total_paid", paid);
            s.put("total_owed", owed);
            s.put("balance", balance);
            settlementList.add(s);
        }

        // 결과 구성
        Map<String, Object> result = new HashMap<>();
        result.put("rawList", rawList);
        result.put("settlementList", settlementList);

        log.info("자동 정산 계산 완료 - {}건", rawList.size());
        return result;
    }


    @Override
    public List<Map<String, Object>> getStatistics(Map<String, Object> params) throws Exception {
        // TODO: 통계 로직
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeSettlement(Map<String, Object> params) throws Exception {
        log.info("정산 완료 처리 요청 - params={}", params);

        if (!params.containsKey("month") || !params.containsKey("userId")) {
            throw new IllegalArgumentException("월별 정산을 위한 'month' 또는 'userId'가 누락되었습니다.");
        }

        // 파라미터 추출
        String month = params.get("month").toString();
        String userId = params.get("userId").toString();

        int result = lunchMapper.completeSettlement(params);

        //  결과 검증
        if (result == 0) {
            log.warn("이미 정산 완료된 월이거나 대상이 없습니다. (month={}, user_id={})", month, userId);
        } else {
            log.info("정산 완료 처리 성공 (month={}, user_id={})", month, userId);
        }
        return result;
    }
}