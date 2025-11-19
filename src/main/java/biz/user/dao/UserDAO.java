package biz.user.dao;

import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("userDAO")
public class UserDAO extends EgovAbstractMapper {

    public int selectCountByUserId(String userId) {
        return selectOne("UserDAO.selectCountByUserId", userId);
    }

    public void insertUser(biz.user.vo.UserVO vo) {
        insert("UserDAO.insertUser", vo);
    }

    public List<UserVO> selectUserList(UserSearchCond cond) {
        return selectList("UserDAO.selectUserList", cond);
    }

    public UserVO selectUser(String userId) {
        return selectOne("UserDAO.selectUser", userId);
    }

    /**
     * 수정
     */
    public void updateUser(UserVO vo) {
        update("UserDAO.updateUser", vo);
    }

    public void deleteUser(String userId) {
        delete("UserDAO.deleteUser", userId);
    }

    public int countUserList(UserSearchCond cond) {
        return selectOne("UserDAO.countUserList", cond);
    }

    public List<String> findUserByName(String userName) {
        return selectList("UserDAO.findUserIdByName",userName);
    }

    public List<UserVO> selectUserTotalList() {
        return selectList("UserDAO.selectUserTotalList");
    }

    public List<String> selectUserRoles(String userId) {
        return selectList("UserDAO.selectUserRoles", userId);
    }

    /** 전화번호 중복 (등록용) */
    public int countByPhone(String phone) {
        return selectOne("UserDAO.countByPhone", phone);
        // namespace.id
    }

    /** 전화번호 중복 (수정용 : 자기 자신 제외) */
    public int countByPhoneExceptUser(String phone, String userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("phone", phone);
        param.put("userId", userId);
        return selectOne("UserDAO.countByPhoneExceptUser", param);
    }

    /** 이메일 중복 (등록용) */
    public int countByEmail(String email) {
        return selectOne("UserDAO.countByEmail", email);
    }

    /** 이메일 중복 (수정용 : 자기 자신 제외) */
    public int countByEmailExceptUser(String email, String userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("email", email);
        param.put("userId", userId);
        return selectOne("UserDAO.countByEmailExceptUser", param);
    }

    public void updateUserExceptPw(UserVO vo) {
        update("UserDAO.updateUserExceptPw", vo);
    }

}
