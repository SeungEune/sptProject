package biz.user.web;

import biz.login.vo.LoginVO;
import biz.user.service.UserService;
import biz.user.vo.UserVO;
import biz.util.EgovStringUtil;
import biz.util.SessionUtil;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.exception.custom.NoContentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
@Slf4j
@Controller
public class MypageController {
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/mypage.do")
    public String mypage(Model model){
        try {
            // SessionUtil을 사용한 로그인 정보 확인
            LoginVO loginVO = SessionUtil.getLoginUser();
            UserVO userVO = userService.getUser(loginVO.getUserId());
            model.addAttribute("user", userVO);
            return "user/mypage";
        }
        catch (Exception e) {
            log.error("계정 조회 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.select"));
            return "error/404";
        }
    }
    @GetMapping("/mypage/{id}/update.do")
    public String mypageUpdate(@PathVariable("id") String userId, Model model) {
        try {
            UserVO userVO = userService.getUser(userId);
            model.addAttribute("user", userVO);
            return "user/updateMypage";
        }
        catch (Exception e) {
            log.error("계정 조회 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.select"));
            return "error/404";
        }
    }
    @PostMapping("/mypage/update/{id}.do")
    public String update(@PathVariable("id") String userId,
                         @Valid @ModelAttribute("user") UserVO user,
                         BindingResult binding,
                         Model model, RedirectAttributes rttr) {
        try {

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
                return "user/edit";
            }

            if (binding.hasErrors()) {
                // 에러 나면 다시 "수정 화면" 으로
                return "user/updateMypage";
            }

            user.setUserId(userId);

            if (EgovStringUtil.isEmpty(user.getPassword()) && EgovStringUtil.isEmpty(user.getPasswordChk())) {
                userService.updateUserExceptPw(user);

                // 성공 메시지 flash 로 실어서 보냄
                rttr.addFlashAttribute("alertMessage", "계정이 수정되었습니다.");

                return "redirect:/mypage.do";
            }

            userService.updateUser(user);

            // 성공 메시지 flash 로 실어서 보냄
            rttr.addFlashAttribute("alertMessage", "계정이 수정되었습니다.");

            return "redirect:/mypage.do";
        }
        catch (Exception e) {
            log.error("계정 변경 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.update"));
            return "error/404";
        }
    }

}
