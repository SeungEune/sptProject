package biz.enter.web;

import biz.enter.service.EnterService;
import biz.enter.vo.EnterVO;
import biz.user.service.UserService;
import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Controller
public class EnterController {
    @Resource(name = "enterService")
    private EnterService enterService;

    @Resource(name = "userService")
    private UserService userService;   // ★ 추가

    @GetMapping("/enter/manage")
    public String enterManage(Model model) throws Exception {
        List<EnterVO> list = enterService.getEnterList();
        model.addAttribute("enterList", list);
        return "enter/manage";
    }

    /** 등록 폼 */
    @GetMapping("/enter/create")
    public String createForm(Model model) throws Exception {

        // 폼 바인딩용 EnterVO
        model.addAttribute("enter", new EnterVO());

        UserSearchCond cond = new UserSearchCond();
        cond.setOption("all");

        // ★ 직원 목록 (아이디/이름 등)
        List<UserVO> users = userService.getUserList(cond);
        model.addAttribute("userList", users);

        return "enter/create";   // 지금 쓰고 있는 템플릿 이름
    }

    @PostMapping("/enter/create")
    public String create(@Valid @ModelAttribute("enter") EnterVO enter,
                         BindingResult binding) throws Exception {

        // 게스트인데 마감일이 없으면 에러 체크하고 싶으면 여기서 검증
        if ("GUEST".equals(enter.getType()) && enter.getEndGuestDt() == null) {
            binding.rejectValue("endGuestDt","required","게스트 마감일을 입력하세요.");
        }

        if (binding.hasErrors()) {
            System.out.println("~~~~~");
            return "enter/create";
        }
        else{
            System.out.println("#####");
        }
        System.out.println(enter);
        System.out.println("~~~~~~~~~");
        enterService.createEnter(enter);
        System.out.println("!!!!");
        return "redirect:/enter/manage";
    }

    // EnterController.java

    @GetMapping("/enter/edit/{id}")
    public String editForm(@PathVariable("id") Long enterId,
                           Model model) throws Exception {

        // 1건 조회
        EnterVO enter = enterService.getEnter(enterId);
        model.addAttribute("enter", enter);

        UserSearchCond cond = new UserSearchCond();
        cond.setOption("all");

        // 직원 선택용 리스트 (EMP일 때 셀렉트 박스 채우려고)
        List<UserVO> users = userService.getUserList(cond);
        model.addAttribute("userList", users);

        return "enter/edit";   // 아래에서 만들 템플릿
    }

    @PostMapping("/enter/edit/{id}")
    public String update(@PathVariable("id") Long enterId,
                         @ModelAttribute("enter") EnterVO enter) throws Exception {

        enter.setEnterId(enterId);   // 혹시 폼에 없으면 보정
        enterService.updateEnter(enter);

        return "redirect:/enter/manage";
    }

    // 삭제 확인 팝업
    @GetMapping("/enter/delete/{id}/confirm")
    public String deleteConfirm(@PathVariable("id") Long enterId, Model model) {
        model.addAttribute("enterId", enterId);
        return "enter/popup";   // 방금 HTML 파일 이름
    }

    // 실제 삭제 처리 (POST 권장)
    @PostMapping("/enter/delete/{id}")
    public String delete(@PathVariable("id") Long enterId) throws Exception {
        enterService.deleteEnter(enterId);
        return "redirect:/enter/manage";
    }


}
