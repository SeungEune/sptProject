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
            @RequestParam(value = "registMonth", required = false) String registMonth,
            @RequestParam(value = "page",        required = false, defaultValue = "1") int page,
            @RequestParam(value = "size",        required = false, defaultValue = "10") int size,
            Model model) throws Exception {

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        UserSearchCond cond = new UserSearchCond();
        cond.setOption(option);
        cond.setKeyword(keyword);
        cond.setJssfcCd(jssfcCd);
        cond.setRegistMonth(registMonth);
        cond.setPage(page);
        cond.setSize(size);
        cond.setOffset((page - 1) * size);

        // 전체 개수
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
                       @Valid @ModelAttribute("user") UserVO user,
                       BindingResult binding,
                       Model model) throws Exception {

        // 비밀번호 확인
        if (!user.getPassword().equals(user.getPasswordChk())) {
            binding.rejectValue("passwordChk", "mismatch", "비밀번호가 일치하지 않습니다.");
        }

        if (binding.hasErrors()) {
            // ★ 다시 수정 모드로 돌려줘야 버튼이 뜸
            model.addAttribute("mode", "edit");
            return "account/edit";
        }

        user.setUserId(userId);
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
