package biz.user.web;

import biz.login.vo.LoginVO;
import biz.user.service.UserService;
import biz.user.vo.UserVO;
import biz.util.SessionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
public class MypageController {
    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("mypage")
    public String mypage(Model model) throws Exception {
        // SessionUtil을 사용한 로그인 정보 확인
        LoginVO loginVO = SessionUtil.getLoginUser();
        UserVO userVO = userService.getUser(loginVO.getUserId());
        model.addAttribute("user", userVO);
        return "account/mypage";
    }
    @GetMapping("mypage/{id}/update")
    public String mypageUpdate(@PathVariable("id") String userId, Model model) throws Exception {
        UserVO userVO = userService.getUser(userId);
        model.addAttribute("user", userVO);
        return "account/updateMypage";
    }
    @PostMapping("/mypage/update/{id}")
    public String update(@PathVariable("id") String userId,
                         @Valid @ModelAttribute("user") UserVO user,
                         BindingResult binding,
                         Model model) throws Exception {

        // 비밀번호 확인
        if (!user.getPassword().equals(user.getPasswordChk())) {
            binding.rejectValue("passwordChk", "mismatch", "비밀번호가 일치하지 않습니다.");
        }

        if (binding.hasErrors()) {
            // 에러 나면 다시 "수정 화면" 으로
            return "account/updateMypage";
        }

        user.setUserId(userId);
        userService.updateUser(user);
        return "redirect:/mypage";
    }

}
