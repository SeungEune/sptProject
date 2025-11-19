package biz.user.service.impl;

import biz.user.dao.UserDAO;
import biz.user.service.UserService;
import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;
import biz.util.SessionUtil;
import biz.util.EgovFileScrty;
import lombok.RequiredArgsConstructor;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("userService")
public class UserServiceImpl extends EgovAbstractServiceImpl implements UserService {
    @Resource(name = "userDAO")
    private UserDAO userDAO;

    @Override
    public boolean isDuplicatedId(String userId) {
        return userDAO.selectCountByUserId(userId) > 0;
    }

    @Transactional
    @Override
    public void createUser(UserVO vo) throws Exception {
        // 등록자(관리자) 세팅(선택)
        if (vo.getRegisterId() == null) {
            var login = SessionUtil.getLoginUser();
            if (login != null) vo.setRegisterId(login.getUserId());
        }

        // 비밀번호 암호화 (eGov 규칙 준수)
        String enc = EgovFileScrty.encryptPassword(vo.getPassword(), vo.getUserId());
        vo.setPassword(enc);

        // DB INSERT
        userDAO.insertUser(vo);
    }

    @Override
    public List<UserVO> getUserList(UserSearchCond cond) throws Exception {
        return userDAO.selectUserList(cond);
    }


    @Override
    public UserVO getUser(String userId) throws Exception {
        return userDAO.selectUser(userId);
    }

    @Transactional
    @Override
    public void updateUser(UserVO vo) throws Exception {
        // 비밀번호를 수정 폼에서 다시 받는다면 암호화
        if (vo.getPassword() != null && !vo.getPassword().isBlank()) {
            String enc = EgovFileScrty.encryptPassword(vo.getPassword(), vo.getUserId());
            vo.setPassword(enc);
        }
        userDAO.updateUser(vo);
    }

    @Override
    public void deleteUser(String userId) throws Exception {
        userDAO.deleteUser(userId);
    }

    @Override
    public int getUserCount(UserSearchCond cond) throws Exception {
        return userDAO.countUserList(cond);
    }

    @Override
    public List<String> getUserByName(String name) throws Exception {
        return userDAO.findUserByName(name);
    }

}
