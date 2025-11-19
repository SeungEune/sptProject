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
    void deleteUser(String userId) throws Exception;

    int getUserCount(UserSearchCond cond) throws Exception;

    List<String> getUserByName(String name)  throws Exception;
}
