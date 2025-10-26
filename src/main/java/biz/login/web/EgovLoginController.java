package biz.login.web;

import biz.login.service.EgovLoginService;
import biz.login.vo.LoginVO;
import biz.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 로그인을 처리하는 컨트롤러 클래스
 * @author 공통서비스 개발팀
 * @since 2024.12.19
 * @version 1.0
 */
@Slf4j
@Controller
public class EgovLoginController {

    @Resource(name = "loginService")
    private EgovLoginService loginService;

    /**
     * 로그인 폼 페이지
     * @param error 에러 메시지 파라미터
     * @param model Model 객체
     * @return 로그인 페이지
     */
    @GetMapping("/login/loginForm.do")
    public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        // 이미 로그인된 사용자는 메인 페이지로 리다이렉트
        if (SessionUtil.isAuthenticated()) {
            return "redirect:/main/mainForm.do";
        }
        
        if ("true".equals(error)) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "login/loginForm";
    }

    /**
     * 로그인 처리
     * @param loginVO 로그인 정보
     * @return 리다이렉트 URL
     */
    @PostMapping("/login/actionLogin.do")
    public String actionLogin(@ModelAttribute LoginVO loginVO) {
        try {

            //LoginVO resultVO = loginService.actionLogin(loginVO);

            //임시 더미 사용자 처리
            LoginVO resultVO = new LoginVO();
            resultVO.setUserId(loginVO.getUserId());
            resultVO.setPassword(loginVO.getPassword());
            resultVO.setLoginType("COM");
            resultVO.setUserSe("USR");
            resultVO.setNickname("스패셜티");

            if (resultVO != null && resultVO.getUserId() != null && !resultVO.getUserId().equals("")) {
                SessionUtil.setAttribute("LoginVO", resultVO);
                return "redirect:/main/mainForm.do";
            } else {
                return "redirect:/login/loginForm.do?error=true";
            }
        } catch (Exception e) {
            return "redirect:/login/loginForm.do?error=true";
        }
    }

    /**
     * 로그아웃 처리
     * @param request HttpServletRequest
     * @return 리다이렉트 URL
     */
    @GetMapping("/login/logout.do")
    public String logout(HttpServletRequest request) {
        SessionUtil.removeAttribute("LoginVO");
        request.getSession().invalidate();
        return "redirect:/login/loginForm.do";
    }
}

