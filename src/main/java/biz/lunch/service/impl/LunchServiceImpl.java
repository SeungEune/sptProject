package biz.lunch.service.impl;

import biz.lunch.dao.LunchDAO;
import biz.lunch.service.LunchService;
import biz.lunch.vo.LunchVO;
import biz.lunch.vo.ParticipantVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import biz.util.EgovStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("lunchService")
public class LunchServiceImpl extends EgovAbstractServiceImpl implements LunchService {

    @Resource(name = "lunchDAO")
    private LunchDAO lunchDAO;

    // 정산 기준일 (예: 전월 26일 ~ 당월 25일)
    @Value("${lunch.settlement.start-day:26}")
    private int startDay;

    @Value("${lunch.settlement.end-day:25}")
    private int endDay;

    /**
     * 날짜 문자열(yyyy-MM-dd)을 기반으로 해당 월의 통계 정보를 갱신한다.
     */
    private void updateSummary(String dateStr) throws Exception {
        if (EgovStringUtil.isEmpty(dateStr) || dateStr.length() < 7) {
            return;
        }
        String month = dateStr.substring(0, 7); // yyyy-MM
        lunchDAO.updateSummaryAfterChange(month);
    }

    @Override
    public List<UserVO> getUserList() throws Exception {
        return lunchDAO.getUserList();
    }

    @Override
    public int registerLunch(LunchVO lunchVO) throws Exception {
        // 1. 화면에서 넘어온 배열 데이터(ID, 금액)를 List<ParticipantVO>로 변환
        lunchVO.makeParticipantList();

        // 2. 총 금액 계산 (프론트에서 계산해서 넘겨주더라도 서버에서 재검증)
        int totalAmount = 0;
        if (lunchVO.getParticipantList() != null) {
            for (ParticipantVO p : lunchVO.getParticipantList()) {
                totalAmount += p.getIndividualAmount();
            }
        }
        lunchVO.setTotalAmount(totalAmount);

        // 3. Lunch Master 등록 (insert 후 selectKey를 통해 lunchVO.lunchId에 값이 세팅됨)
        int result = lunchDAO.registerLunch(lunchVO);

        // 4. 참여자 정보 등록
        if (lunchVO.getLunchId() != null) {
            if (lunchVO.getParticipantList() != null && !lunchVO.getParticipantList().isEmpty()) {
                // 생성된 lunchId를 참여자 객체에 주입
                for (ParticipantVO p : lunchVO.getParticipantList()) {
                    p.setLunchId(lunchVO.getLunchId());
                }
                lunchDAO.insertParticipantsBatch(lunchVO);
            }
            // 5. 통계 갱신
            updateSummary(lunchVO.getDate());
        }
        return result;
    }

    @Override
    public int updateLunch(LunchVO lunchVO) throws Exception {
        // 1. 데이터 가공
        lunchVO.makeParticipantList();
        // 총액 재계산
        int totalAmount = 0;
        if (lunchVO.getParticipantList() != null) {
            for (ParticipantVO p : lunchVO.getParticipantList()) {
                totalAmount += p.getIndividualAmount();
            }
        }
        lunchVO.setTotalAmount(totalAmount);
        // 2. 기존 날짜 조회 (날짜가 변경되었을 경우 이전 달 통계도 갱신 필요)
        String oldDate = lunchDAO.getLunchDateById(lunchVO.getLunchId());
        int result = lunchDAO.updateLunch(lunchVO);

        // 4. 기존 참여자 삭제 후 재등록
        lunchDAO.deleteParticipantsByLunchId(lunchVO.getLunchId());

        if (lunchVO.getParticipantList() != null && !lunchVO.getParticipantList().isEmpty()) {
            for (ParticipantVO p : lunchVO.getParticipantList()) {
                p.setLunchId(lunchVO.getLunchId());
            }
            lunchDAO.insertParticipantsBatch(lunchVO);
        }

        // 5. 통계 갱신 (변경 전 월, 변경 후 월 모두)
        updateSummary(lunchVO.getDate());
        if (oldDate != null && !oldDate.substring(0, 7).equals(lunchVO.getDate().substring(0, 7))) {
            updateSummary(oldDate);
        }
        return result;
    }

    @Override
    public int deleteLunch(int lunchId) throws Exception {
        // 삭제 전 날짜 조회 (통계 갱신용)
        String dateStr = lunchDAO.getLunchDateById(lunchId);
        // 참여자 삭제
        lunchDAO.deleteParticipantsByLunchId(lunchId);
        // 마스터 삭제
        int result = lunchDAO.deleteLunch(lunchId);
        // 통계 갱신
        if (result > 0) {
            updateSummary(dateStr);
        }
        return result;
    }

    @Override
    public List<LunchVO> getLunchList(LunchVO searchVO) throws Exception {
        // 날짜 범위 검색 조건 설정 (YYYY-MM이 들어오면 해당 월의 정산 기간으로 변환)
        if (searchVO != null && !EgovStringUtil.isEmpty(searchVO.getDate()) && searchVO.getDate().length() == 7) {
            String monthStr = searchVO.getDate();
            YearMonth yearMonth = YearMonth.parse(monthStr);

            // 예: 2024-07 검색 -> 2024-06-26 ~ 2024-07-25
            LocalDate startDate = yearMonth.atDay(1).minusMonths(1).withDayOfMonth(startDay);
            LocalDate endDate = yearMonth.atDay(endDay);

            searchVO.setStartDate(startDate.toString());
            searchVO.setEndDate(endDate.toString());
        }
        return lunchDAO.getLunchList(searchVO);
    }

    @Override
    public List<SummaryVO> getStatistics(String searchMonth) throws Exception {
        String startDate = null;
        String endDate = null;

        if (!EgovStringUtil.isEmpty(searchMonth) && searchMonth.length() == 7) {
            YearMonth yearMonth = YearMonth.parse(searchMonth);
            LocalDate start = yearMonth.atDay(1).minusMonths(1).withDayOfMonth(startDay);
            LocalDate end = yearMonth.atDay(endDay);
            startDate = start.toString();
            endDate = end.toString();
        }
        return lunchDAO.getStatistics(startDate, endDate, searchMonth);
    }

    @Override
    public int completeSettlement(String month, String userId, String action) throws Exception {
        if (EgovStringUtil.isEmpty(month) || EgovStringUtil.isEmpty(userId)) {
            throw new IllegalArgumentException("필수 파라미터 누락");
        }
        //조회할 때처럼 날짜 범위(startDate, endDate)를 계산해서 넘겨야 함
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1).minusMonths(1).withDayOfMonth(startDay);
        LocalDate endDate = yearMonth.atDay(endDay);

        String finalAction = EgovStringUtil.isEmpty(action) ? "complete" : action;

        return lunchDAO.completeSettlement(startDate.toString(), endDate.toString(), userId, finalAction);
    }
}