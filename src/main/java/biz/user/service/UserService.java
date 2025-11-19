package biz.user.service;

import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;

import java.util.List;

public interface UserService {
    boolean isDuplicatedId(String userId);
    void createUser(UserVO vo) throws Exception;
    // UserService
    List<UserVO> getUserList(UserSearchCond cond);
    UserVO getUser(String userId);   // ✅ 추가
    void updateUser(UserVO vo) throws Exception;      // ✅ 추가
    void deleteUser(String userId);

    int getUserCount(UserSearchCond cond);

    List<UserVO> getUserTotalList();

    List<String> getUserRoles(String userId) throws Exception;

    boolean isDuplicatedPhone(String phone);
    boolean isDuplicatedPhoneExceptUser(String phone, String userId);

    boolean isDuplicatedEmail(String email);
    boolean isDuplicatedEmailExceptUser(String email, String userId);

    void updateUserExceptPw(UserVO vo);
}
