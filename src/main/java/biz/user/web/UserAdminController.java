package biz.user.web;

import biz.user.service.UserService;
import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;
import biz.util.EgovStringUtil;
import egovframework.com.cmm.EgovMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import org.springframework.ui.Model;   // ✅ 이걸로 교체
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserAdminController {
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

   @Resource(name = "userService")
   private  UserService userService;

    // 등록 폼
    @GetMapping("/create.do")
    public String createForm(@ModelAttribute("user") UserVO user) {
        // 비어 있는 user를 모델에 넣어서 폼에서 사용
        return "user/create";
    }


    /** 아이디 중복 체크 (폼의 formaction에서 호출) */
    @GetMapping("/create/id-check.do")
    @ResponseBody
    public boolean idCheck(@RequestParam("userId") String userId) {
        return userService.isDuplicatedId(userId);
    }

    /** 등록 처리 */
    @PostMapping("/create.do")
    public String create(@Valid @ModelAttribute("user") UserVO user,
                         BindingResult binding, Model model, RedirectAttributes rttr) {
        try {
            // 비밀번호 확인
            if (!user.getPassword().equals(user.getPasswordChk())) {
                binding.rejectValue("passwordChk", "mismatch", "비밀번호가 일치하지 않습니다.");
            }

            // 중복 확인
            if (userService.isDuplicatedId(user.getUserId())) {
                binding.rejectValue("userId", "duplicated", "이미 사용 중인 아이디입니다.");
            }

            // 전화번호
            if (userService.isDuplicatedPhone(user.getPhone())) {
                binding.rejectValue("phone", "duplicated", "이미 사용 중인 전화번호입니다.");
            }
            // 이메일
            if (userService.isDuplicatedEmail(user.getEmail())) {
                binding.rejectValue("email", "duplicated", "이미 사용 중인 이메일입니다.");
            }


            if (binding.hasErrors()) {
                return "user/create";
            }

            userService.createUser(user);
            // 성공 메시지 flash 로 실어서 보냄
            rttr.addFlashAttribute("alertMessage", "계정이 등록되었습니다.");

            return "redirect:/user/manage.do";
        }
        catch (Exception e) {
            log.error("계정 등록 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.insert"));
            return "error/404";
        }
    }


    @GetMapping("/manage.do")
    public String manage(
            @RequestParam(value = "option",      required = false, defaultValue = "all") String option,
            @RequestParam(value = "keyword",     required = false) String keyword,
            @RequestParam(value = "jssfcCd",     required = false) String jssfcCd,
            //  새로 추가
            @RequestParam(value = "startDate",   required = false) String startDate,
            @RequestParam(value = "endDate",     required = false) String endDate,
            @RequestParam(value = "page",        required = false, defaultValue = "1") int page,
            @RequestParam(value = "size",        required = false, defaultValue = "10") int size,
            Model model) {

      try {
          if (page < 1) page = 1;
          if (size < 1) size = 10;

          UserSearchCond cond = new UserSearchCond();
          cond.setOption(option);
          cond.setKeyword(keyword);
          cond.setJssfcCd(jssfcCd);

          //  기간값 세팅
          cond.setStartDate(startDate);
          cond.setEndDate(endDate);

          cond.setPage(page);
          cond.setSize(size);
          cond.setOffset((page - 1) * size);

          int totalCount = userService.getUserCount(cond);
          int totalPages = (int) Math.ceil((double) totalCount / size);

          List<UserVO> userList = userService.getUserList(cond);

          model.addAttribute("userList", userList);
          model.addAttribute("search", cond);
          model.addAttribute("page", page);
          model.addAttribute("size", size);
          model.addAttribute("totalPages", totalPages);
          model.addAttribute("totalCount", totalCount);

          return "user/manage";
      }
      catch (Exception e) {
          log.error("계정목록 조회 실패", e);
          model.addAttribute("message", egovMessageSource.getMessage("fail.common.select"));
          return "error/404";
      }
    }






    /** 수정 폼 */
// 조회(읽기 전용 화면)
    @GetMapping("/edit/{id}.do")
    public String editView(@PathVariable("id") String userId, Model model)  {
        try {
            UserVO user = userService.getUser(userId);
            model.addAttribute("user", user);
            model.addAttribute("mode", "view");   // 조회 모드

            return "user/edit";
        }
        catch (Exception e) {
            log.error("계정 조회 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.select"));
            return "error/404";
        }
    }
    // 수정 화면(입력 가능)
    @GetMapping("/edit/{id}/update.do")
    public String editForm(@PathVariable("id") String userId, Model model){
        try {
            UserVO user = userService.getUser(userId);
            model.addAttribute("user", user);
            model.addAttribute("mode", "edit");   // 수정 모드
            return "user/edit";
        }
        catch (Exception e) {
            log.error("계정 조회 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.select"));
            return "error/404";
        }
    }

    @PostMapping("/edit/{id}.do")
    public String edit(@PathVariable("id") String userId,
                       @ModelAttribute("user") UserVO user,
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

            // 4) 에러 있으면 다시 수정 화면
            if (binding.hasErrors()) {
                model.addAttribute("mode", "edit");
                return "user/edit";
            }


            // 5) userId는 path variable 기준으로 고정
            user.setUserId(userId);

            if (EgovStringUtil.isEmpty(user.getPassword()) && EgovStringUtil.isEmpty(user.getPasswordChk())) {
                // 성공 메시지 flash 로 실어서 보냄
                rttr.addFlashAttribute("alertMessage", "계정이 수정되었습니다.");

                userService.updateUserExceptPw(user);
                return "redirect:/user/manage.do";
            }

            userService.updateUser(user);

            // 성공 메시지 flash 로 실어서 보냄
            rttr.addFlashAttribute("alertMessage", "계정이 수정되었습니다.");

            return "redirect:/user/manage.do";
        }
        catch (Exception e) {
            log.error("계정 변경 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.update"));
            return "error/404";
        }
    }

    // 실제 삭제 처리 (POST 권장)
    @PostMapping("/delete/{id}.do")
    public String delete(@PathVariable("id") String userId, Model model, RedirectAttributes rttr) {
        try {
            userService.deleteUser(userId);

            // 성공 메시지 flash 로 실어서 보냄
            rttr.addFlashAttribute("alertMessage", "계정이 삭제되었습니다.");

            return "redirect:/user/manage.do";
        }
        catch (Exception e) {
            log.error("계정 삭제 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.delete"));
            return "error/404";
        }
    }





}
