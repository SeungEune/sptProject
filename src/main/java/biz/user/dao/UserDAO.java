package biz.user.dao;

import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public UserVO selectUser(String userId){
        return selectOne("UserDAO.selectUser", userId);
    }

    /** 수정 */
    public void updateUser(UserVO vo){
        update("UserDAO.updateUser", vo);
    }

    public void deleteUser(String userId){
        delete("UserDAO.deleteUser", userId);
    }

    public int countUserList(UserSearchCond cond) {
        return selectOne("UserDAO.countUserList", cond);
    }

    public List<String> findUserByName(String userName) {
        return selectList("UserDAO.findUserIdByName",userName);
    }
}
