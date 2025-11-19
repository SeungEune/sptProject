package biz.user.web;

import biz.user.service.UserService;
import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import org.springframework.ui.Model;   // ✅ 이걸로 교체
import javax.validation.Valid;
import java.util.List;

@Controller
public class AccountAdminController {
   @Resource(name = "userService")
   private  UserService userService;

    /** 등록 폼 */
    @GetMapping("/account/create")
    public String createForm(@ModelAttribute("user") UserVO user) {
        return "account/create";
    }

    /** 아이디 중복 체크 (폼의 formaction에서 호출) */
    @GetMapping("/account/create/id-check")
    @ResponseBody
    public String idCheck(@RequestParam("userId") String userId) {
        boolean dup = userService.isDuplicatedId(userId);
        return dup ? "DUPLICATED" : "OK";
    }

    /** 등록 처리 */
    @PostMapping("/account/create")
    public String create(@Valid @ModelAttribute("user") UserVO user,
                         BindingResult binding, Model model) throws Exception {
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
            return "account/create";
        }

        userService.createUser(user);
        return "redirect:/account/manage";
    }


    @GetMapping("/account/manage")
    public String manage(
            @RequestParam(value = "option",      required = false, defaultValue = "all") String option,
            @RequestParam(value = "keyword",     required = false) String keyword,
            @RequestParam(value = "jssfcCd",     required = false) String jssfcCd,
            //  새로 추가
            @RequestParam(value = "startDate",   required = false) String startDate,
            @RequestParam(value = "endDate",     required = false) String endDate,
            @RequestParam(value = "page",        required = false, defaultValue = "1") int page,
            @RequestParam(value = "size",        required = false, defaultValue = "10") int size,
            Model model) throws Exception {

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

        return "account/manage";
    }






    /** 수정 폼 */
// 조회(읽기 전용 화면)
    @GetMapping("/account/edit/{id}")
    public String editView(@PathVariable("id") String userId, Model model) throws Exception {
        UserVO user = userService.getUser(userId);
        model.addAttribute("user", user);
        model.addAttribute("mode", "view");   // ★ 조회 모드

        System.out.println("~~~~~~~~~~~~~~~~~~");
        System.out.println(user);
        return "account/edit";
    }
    // 수정 화면(입력 가능)
    @GetMapping("/account/edit/{id}/update")
    public String editForm(@PathVariable("id") String userId, Model model) throws Exception {
        UserVO user = userService.getUser(userId);
        model.addAttribute("user", user);
        model.addAttribute("mode", "edit");   // ★ 수정 모드
        return "account/edit";
    }

    @PostMapping("/account/edit/{id}")
    public String edit(@PathVariable("id") String userId,
                       @ModelAttribute("user") UserVO user,
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

        // 4) 에러 있으면 다시 수정 화면
        if (binding.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "account/edit";
        }


        // 5) userId는 path variable 기준으로 고정
        user.setUserId(userId);

        if(user.getPassword().isEmpty()&&user.getPasswordChk().isEmpty()){
            userService.updateUserExceptPw(user);
            return "redirect:/account/manage";
        }
        userService.updateUser(user);
        return "redirect:/account/manage";
    }



    // 삭제 확인 팝업
    @GetMapping("/account/delete/{id}/confirm")
    public String deleteConfirm(@PathVariable("id") String userId, Model model) {
        model.addAttribute("userId", userId);
        return "account/popup";   // 방금 HTML 파일 이름
    }

    // 실제 삭제 처리 (POST 권장)
    @PostMapping("/account/delete/{id}")
    public String delete(@PathVariable("id") String userId) throws Exception {
        userService.deleteUser(userId);
        return "redirect:/account/manage";
    }





}
