package biz.user.service;

import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;

import java.util.List;

public interface UserService {
    boolean isDuplicatedId(String userId);
    void createUser(UserVO vo) throws Exception;
    // UserService
    List<UserVO> getUserList(UserSearchCond cond) throws Exception;
    UserVO getUser(String userId) throws Exception;   // ✅ 추가
    void updateUser(UserVO vo) throws Exception;      // ✅ 추가
    void deleteUser(String userId)  throws Exception;

    int getUserCount(UserSearchCond cond) throws Exception;

    List<UserVO> getUserTotalList() throws Exception;

    List<String> getUserRoles(String userId) throws Exception;

    boolean isDuplicatedPhone(String phone) throws Exception;
    boolean isDuplicatedPhoneExceptUser(String phone, String userId) throws Exception;

    boolean isDuplicatedEmail(String email) throws Exception;
    boolean isDuplicatedEmailExceptUser(String email, String userId) throws Exception;

    void updateUserExceptPw(UserVO vo) throws Exception;

    List<String> getUserByName(String userName) throws Exception;
}
