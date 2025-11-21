package biz.lunch.dao;

import biz.lunch.vo.LunchVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import egovframework.com.cmm.service.impl.EgovComAbstractDAO; // [중요] 프로젝트 내 공통 DAO 상속
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 점심식대 관련 처리를 하는 비즈니스 구현 클래스
 */
@Repository("lunchDAO")
public class LunchDAO extends EgovComAbstractDAO { //

    private static final String NAMESPACE = "biz.lunch.dao.LunchDAO.";

    /**
     * 점심식대 정보를 등록한다.
     * @param lunchVO - 등록할 정보가 담긴 LunchVO
     * @return int
     * @exception Exception
     */
    public int registerLunch(LunchVO lunchVO) throws Exception {
        insert(NAMESPACE + "registerLunch", lunchVO);
        return 1;
    }

    /**
     * 점심식대 정보를 수정한다.
     * @param lunchVO - 수정할 정보가 담긴 LunchVO
     * @return int
     * @exception Exception
     */
    public int updateLunch(LunchVO lunchVO) throws Exception {
        return update(NAMESPACE + "updateLunch", lunchVO);
    }

    /**
     * 점심식대 ID에 해당하는 참여자 정보를 삭제한다.
     * @param lunchId - 삭제할 점심식대 ID
     * @return int
     * @exception Exception
     */
    public int deleteParticipantsByLunchId(int lunchId) throws Exception {
        return delete(NAMESPACE + "deleteParticipantsByLunchId", lunchId);
    }

    /**
     * 점심식대 참여자 정보를 일괄 등록한다.
     * @param lunchVO - 등록할 참여자 정보가 담긴 LunchVO
     * @return int
     * @exception Exception
     */
    public int insertParticipantsBatch(LunchVO lunchVO) throws Exception {
        insert(NAMESPACE + "insertParticipantsBatch", lunchVO);
        return 1;
    }

    /**
     * 점심식대 목록을 조회한다.
     * @param searchVO - 조회할 정보가 담긴 LunchVO
     * @return List<LunchVO>
     * @exception Exception
     */
    public List<LunchVO> getLunchList(LunchVO searchVO) throws Exception {
        return selectList(NAMESPACE + "getLunchList", searchVO);
    }

    /**
     * 점심식대 통계 정보를 조회한다.
     * @param startDate - 조회 시작일
     * @param endDate - 조회 종료일
     * @param month - 조회 월
     * @return List<SummaryVO>
     * @exception Exception
     */
    public List<SummaryVO> getStatistics(String startDate, String endDate, String month) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("month", month);
        return selectList(NAMESPACE + "getStatistics", params);
    }

    /**
     * 정산을 완료 처리한다.
     * @param userId - 사용자 ID
     * @param action - 처리 액션
     * @return int
     * @exception Exception
     */
    public int completeSettlement(String startDate, String endDate, String userId, String action) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate); // 추가됨
        params.put("endDate", endDate);     // 추가됨
        params.put("userId", userId);
        params.put("action", action);

        return update(NAMESPACE + "completeSettlement", params);
    }

    /**
     * 점심식대 정보를 삭제한다.
     * @param lunchId - 삭제할 점심식대 ID
     * @return int
     */
    public int deleteLunch(int lunchId) {
        return delete(NAMESPACE + "deleteLunch", lunchId);
    }

    /**
     * 변경사항 발생 후 월별 요약 정보를 업데이트한다.
     * @param month - 업데이트할 월
     * @return int
     * @exception Exception
     */
    public int updateSummaryAfterChange(String month, String representativeName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("month", month);
        params.put("representativeName", representativeName);
        return insert(NAMESPACE + "updateSummaryAfterChange", params);
    }

    /**
     * 점심식대 ID로 날짜를 조회한다.
     * @param lunchId - 조회할 점심식대 ID
     * @return String - 점심 날짜
     * @exception Exception
     */
    public String getLunchDateById(int lunchId) throws Exception {
        return selectOne(NAMESPACE + "getLunchDateById", lunchId);
    }

    /**
     * 전체 사용자 목록을 조회한다.
     * @return List<UserVO>
     * @exception Exception
     */
    public List<UserVO> getUserList() throws Exception {
        return selectList(NAMESPACE + "getUserList");
    }
}