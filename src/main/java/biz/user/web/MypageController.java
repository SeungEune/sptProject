package biz.user.web;

import biz.login.vo.LoginVO;
import biz.user.service.UserService;
import biz.user.vo.UserVO;
import biz.util.SessionUtil;
import egovframework.com.cmm.exception.custom.NoContentException;
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
    public String mypage(Model model){
        // SessionUtil을 사용한 로그인 정보 확인
        LoginVO loginVO = SessionUtil.getLoginUser();
        UserVO userVO = userService.getUser(loginVO.getUserId());
        if(userVO == null) throw new NoContentException("직원이 없습니다.");
        model.addAttribute("user", userVO);
        return "account/mypage";
    }
    @GetMapping("mypage/{id}/update")
    public String mypageUpdate(@PathVariable("id") String userId, Model model) {
        UserVO userVO = userService.getUser(userId);
        if(userVO==null)throw new NoContentException("직원이 없습니다.");
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

        // 전화번호 중복 (현재 userId 제외)
        if (userService.isDuplicatedPhoneExceptUser(user.getPhone(), userId)) {
            binding.rejectValue("phone", "duplicated", "이미 사용 중인 전화번호입니다.");
        }

        // 이메일 중복 (현재 userId 제외)
        if (userService.isDuplicatedEmailExceptUser(user.getEmail(), userId)) {
            binding.rejectValue("email", "duplicated", "이미 사용 중인 이메일입니다.");
        }

        if (binding.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "account/edit";
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
