package biz.lunch.service;

import biz.lunch.vo.LunchVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;

import java.util.List;

/**
 * 점심/커피 정산 관련 서비스 인터페이스
 */
public interface LunchService {
    /**
     * 전체 사용자 목록을 조회한다.
     * @return List<UserVO> - 사용자 목록
     * @throws Exception
     */
    List<UserVO> getUserList() throws Exception;

    /**
     * 점심/커피 내역을 등록한다.
     * @param lunchVO - 등록할 정보가 담긴 LunchVO
     * @return int - 등록 결과
     * @throws Exception
     */
    int registerLunch(LunchVO lunchVO) throws Exception;

    /**
     * 점심/커피 내역을 수정한다.
     * @param lunchVO - 수정할 정보가 담긴 LunchVO
     * @return int - 수정 결과
     * @throws Exception
     */
    int updateLunch(LunchVO lunchVO) throws Exception;

    /**
     * 점심/커피 내역을 삭제한다.
     * @param lunchId - 삭제할 점심식대 ID
     * @return int - 삭제 결과
     * @throws Exception
     */
    int deleteLunch(int lunchId) throws Exception;

    /**
     * 점심/커피 목록을 조회한다.
     * @param searchVO - 조회할 정보가 담긴 LunchVO
     * @return List<LunchVO> - 점심/커피 목록
     * @throws Exception
     */
    List<LunchVO> getLunchList(LunchVO searchVO) throws Exception;

    /**
     * 월별 통계 정보를 조회한다.
     * @param searchMonth - 조회할 월 (yyyy-MM)
     * @return List<SummaryVO> - 통계 정보 목록
     * @throws Exception
     */
    List<SummaryVO> getStatistics(String searchMonth) throws Exception;

    /**
     * 정산 완료/취소 처리를 한다.
     * @param month - 정산 월 (yyyy-MM)
     * @param userId - 사용자 ID
     * @param action - 처리 액션 (complete or cancel)
     * @return int - 처리 결과
     * @throws Exception
     */
    int completeSettlement(String month, String userId, String action) throws Exception;
}
