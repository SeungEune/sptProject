package biz.lunch.service.impl;

import biz.lunch.dao.LunchMapper;
import biz.lunch.service.LunchService;
import biz.lunch.vo.LunchVO;
import biz.lunch.vo.ParticipantVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service("lunchService")
public class LunchServiceImpl implements LunchService {

    @Resource(name = "lunchMapper")
    private LunchMapper lunchMapper;

    /**
     * 요약 테이블 자동 갱신
     */
    private void updateSummary(String dateStr) throws Exception {
        if (dateStr == null || dateStr.isEmpty()) {
            log.warn("date 파라미터가 null이거나 비어 있습니다. 요약 갱신을 건너뜁니다.");
            return;
        }
        // 날짜 문자열에서 월만 추출
        String month = dateStr.substring(0, 7);
        log.info("요약 테이블 갱신 시작 (month={})", month);
        lunchMapper.updateSummaryAfterChange(month);
    }

    /**
     * 사용자 목록 조회
     */
    @Override
    public List<UserVO> getUserList() throws Exception {
        return lunchMapper.getUserList();
    }

    /**
     * 점심/커피 내역 등록
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int registerLunch(LunchVO lunchVO) throws Exception {
        
        // 1. 총 금액 계산
        int totalAmount = 0;
        if (lunchVO.getParticipantList() != null) {
            for (ParticipantVO p : lunchVO.getParticipantList()) {
                totalAmount += p.getIndividualAmount();
            }
        }
        lunchVO.setTotalAmount(totalAmount);
        
        // 2. lunch_master에 데이터 삽입
        int masterResult = lunchMapper.registerLunch(lunchVO);
        
        if (masterResult > 0 && lunchVO.getLunchId() != null) {
            // 3. 참여자 등록
            if (lunchVO.getParticipantList() != null && !lunchVO.getParticipantList().isEmpty()) {
                lunchMapper.insertParticipantsBatch(lunchVO);
                log.debug("참여자 등록 완료");
            } else {
                log.warn("참여자가 0명이므로 건너뜁니다.");
            }
            
            // 4. 요약 테이블 갱신
            updateSummary(lunchVO.getDate());
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
    public int updateLunch(LunchVO lunchVO) throws Exception {
        
        // 1. 총 금액 계산
        int totalAmount = 0;
        if (lunchVO.getParticipantList() != null) {
            for (ParticipantVO p : lunchVO.getParticipantList()) {
                totalAmount += p.getIndividualAmount();
            }
        }
        lunchVO.setTotalAmount(totalAmount);
        
        // 2. lunch_master 수정
        int result = lunchMapper.updateLunch(lunchVO);
        
        // 3. 기존 참여자 삭제
        lunchMapper.deleteParticipantsByLunchId(lunchVO.getLunchId());
        
        // 4. 새로운 참여자 등록
        if (lunchVO.getParticipantList() != null && !lunchVO.getParticipantList().isEmpty()) {
            lunchMapper.insertParticipantsBatch(lunchVO);
            log.debug("참여자 (수정) 등록 완료");
        } else {
            log.warn("참여자가 0명이므로 건너뜁니다.");
        }
        
        // 5. 요약 테이블 갱신
        updateSummary(lunchVO.getDate());
        
        log.info("점심/커피 내역 수정 완료 (lunchId={})", lunchVO.getLunchId());
        return result;
    }

    /**
     * 점심/커피 내역 삭제
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLunch(int lunchId) throws Exception {
        
        if (lunchId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 lunchId입니다.");
        }
        
        // 삭제 전 날짜 조회
        String dateStr = lunchMapper.getLunchDateById(lunchId);
        
        // 삭제 실행
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
     * 내역 목록 조회
     */
    @Override
    public List<LunchVO> getLunchList(LunchVO searchVO) throws Exception {
        
        // searchMonth가 있으면 정산 기간 계산 (전달 26일 ~ 당월 25일)
        if (searchVO != null && searchVO.getDate() != null && searchVO.getDate().length() == 7) {
            String monthStr = searchVO.getDate();
            YearMonth yearMonth = YearMonth.parse(monthStr);
            
            LocalDate startDate = yearMonth.atDay(1).minusMonths(1).withDayOfMonth(26);
            LocalDate endDate = yearMonth.atDay(25);
            
            // startDate, endDate 필드에 직접 세팅
            searchVO.setStartDate(startDate.toString());
            searchVO.setEndDate(endDate.toString());
            
            log.info("정산 기간: {} ~ {}", startDate, endDate);
        }
        
        return lunchMapper.getLunchList(searchVO);
    }

    /**
     * 통계 요약 조회
     */
    @Override
    public List<SummaryVO> getStatistics(String searchMonth) throws Exception {
        
        String startDate = null;
        String endDate = null;
        
        if (searchMonth != null && searchMonth.length() == 7) {
            YearMonth yearMonth = YearMonth.parse(searchMonth);
            LocalDate start = yearMonth.atDay(1).minusMonths(1).withDayOfMonth(26);
            LocalDate end = yearMonth.atDay(25);
            
            startDate = start.toString();
            endDate = end.toString();
            
            log.info("통계 조회 기간: {} ~ {}", startDate, endDate);
        }
        
        return lunchMapper.getStatistics(startDate, endDate, searchMonth);
    }

    /**
     * 정산 완료 처리
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeSettlement(String month, String userId, String action) throws Exception {
        if (month == null || userId == null) {
            throw new IllegalArgumentException("month 또는 userId 누락");
        }
        
        String finalAction = (action != null) ? action : "complete";
        log.info("정산 처리 ({}): month={}, userId={}", finalAction, month, userId);
        
        int result = lunchMapper.completeSettlement(month, userId, finalAction);
        
        if (result == 0) {
            log.warn("정산 처리 대상 없음 ({}): (month={}, userId={})", finalAction, month, userId);
        } else {
            log.info("정산 처리 성공 ({}): (month={}, userId={})", finalAction, month, userId);
        }
        
        return result;
    }
}
