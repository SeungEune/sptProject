package biz.lunch.service.impl;

import biz.lunch.dao.LunchDAO;
import biz.lunch.service.LunchService;
import biz.lunch.vo.LunchVO;
import biz.lunch.vo.ParticipantVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import biz.util.EgovStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 점심/커피 정산 관련 서비스 구현 클래스
 * @author GUNWOO
 * @since 2024.07.26
 * @version 1.0
 */
@Slf4j
@Service("lunchService")
public class LunchServiceImpl implements LunchService {

    @Resource(name = "lunchDAO")
    private LunchDAO lunchDAO;

    @Value("${lunch.settlement.start-day:26}")
    private int startDay;

    @Value("${lunch.settlement.end-day:25}")
    private int endDay;

    /**
     * 날짜 문자열을 기반으로 월별 요약 정보를 업데이트한다.
     * @param dateStr 날짜 문자열 (yyyy-MM-dd)
     * @throws Exception
     */
    private void updateSummary(String dateStr) throws Exception {
        if (EgovStringUtil.isEmpty(dateStr)) {
            return;
        }
        String month = dateStr.substring(0, 7);
        lunchDAO.updateSummaryAfterChange(month);
    }

    /**
     * 전체 사용자 목록을 조회한다.
     * @return List<UserVO> - 사용자 목록
     * @throws Exception
     */
    @Override
    public List<UserVO> getUserList() throws Exception {
        return lunchDAO.getUserList();
    }

    /**
     * 점심/커피 내역을 등록한다.
     * @param lunchVO - 등록할 정보가 담긴 LunchVO
     * @return int - 등록 결과
     * @throws Exception
     */
    @Override
    public int registerLunch(LunchVO lunchVO) throws Exception {
        int totalAmount = 0;
        if (lunchVO.getParticipantList() != null) {
            for (ParticipantVO p : lunchVO.getParticipantList()) {
                totalAmount += p.getIndividualAmount();
            }
        }
        lunchVO.setTotalAmount(totalAmount);

        int masterResult = lunchDAO.registerLunch(lunchVO);

        if (masterResult > 0 && lunchVO.getLunchId() != null) {
            if (lunchVO.getParticipantList() != null && !lunchVO.getParticipantList().isEmpty()) {
                lunchDAO.insertParticipantsBatch(lunchVO);
            }
            updateSummary(lunchVO.getDate());
        } else {
            throw new Exception("등록 실패");
        }
        return masterResult;
    }

    /**
     * 점심/커피 내역을 수정한다.
     * @param lunchVO - 수정할 정보가 담긴 LunchVO
     * @return int - 수정 결과
     * @throws Exception
     */
    @Override
    public int updateLunch(LunchVO lunchVO) throws Exception {
        int totalAmount = 0;
        if (lunchVO.getParticipantList() != null) {
            for (ParticipantVO p : lunchVO.getParticipantList()) {
                totalAmount += p.getIndividualAmount();
            }
        }
        lunchVO.setTotalAmount(totalAmount);

        int result = lunchDAO.updateLunch(lunchVO);

        lunchDAO.deleteParticipantsByLunchId(lunchVO.getLunchId());

        if (lunchVO.getParticipantList() != null && !lunchVO.getParticipantList().isEmpty()) {
            lunchDAO.insertParticipantsBatch(lunchVO);
        }

        updateSummary(lunchVO.getDate());
        return result;
    }

    /**
     * 점심/커피 내역을 삭제한다.
     * @param lunchId - 삭제할 점심식대 ID
     * @return int - 삭제 결과
     * @throws Exception
     */
    @Override
    public int deleteLunch(int lunchId) throws Exception {
        if (lunchId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 lunchId입니다.");
        }

        String dateStr = lunchDAO.getLunchDateById(lunchId);
        int delMaster = lunchDAO.deleteLunch(lunchId);

        if (delMaster > 0) {
            updateSummary(dateStr);
        }
        return delMaster;
    }

    /**
     * 점심/커피 목록을 조회한다.
     * @param searchVO - 조회할 정보가 담긴 LunchVO
     * @return List<LunchVO> - 점심/커피 목록
     * @throws Exception
     */
    @Override
    public List<LunchVO> getLunchList(LunchVO searchVO) throws Exception {
        if (searchVO != null && !EgovStringUtil.isEmpty(searchVO.getDate()) && searchVO.getDate().length() == 7) {
            String monthStr = searchVO.getDate();

            YearMonth yearMonth = YearMonth.parse(monthStr);
            LocalDate startDate = yearMonth.atDay(1).minusMonths(1).withDayOfMonth(startDay);
            LocalDate endDate = yearMonth.atDay(endDay);

            searchVO.setStartDate(startDate.toString());
            searchVO.setEndDate(endDate.toString());
        }
        return lunchDAO.getLunchList(searchVO);
    }

    /**
     * 월별 통계 정보를 조회한다.
     * @param searchMonth - 조회할 월 (yyyy-MM)
     * @return List<SummaryVO> - 통계 정보 목록
     * @throws Exception
     */
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

    /**
     * 정산 완료/취소 처리를 한다.
     * @param month - 정산 월 (yyyy-MM)
     * @param userId - 사용자 ID
     * @param action - 처리 액션 (complete or cancel)
     * @return int - 처리 결과
     * @throws Exception
     */
    @Override
    public int completeSettlement(String month, String userId, String action) throws Exception {
        if (EgovStringUtil.isEmpty(month) || EgovStringUtil.isEmpty(userId)) {
            throw new IllegalArgumentException("필수 파라미터 누락");
        }

        String finalAction = EgovStringUtil.isEmpty(action) ? "complete" : action;
        return lunchDAO.completeSettlement(month, userId, finalAction);
    }
}