package biz.enter.web;

import biz.enter.service.EnterService;
import biz.enter.vo.EnterVO;
import biz.user.service.UserService;
import biz.user.vo.UserSearchCond;
import biz.user.vo.UserVO;
import biz.util.EgovDateUtil;
import biz.util.EgovStringUtil;
import egovframework.com.cmm.exception.custom.NoContentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/enter")
public class EnterController {
    @Resource(name = "enterService")
    private EnterService enterService;

    @Resource(name = "userService")
    private UserService userService;   // ★ 추가

    @GetMapping("/manage.do")
    public String manage(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model){
        try {
            List<EnterVO> enterList = enterService.getEnterList(page, size);

            int totalCount = enterService.getEnterCount();
            int totalPages = (int) Math.ceil((double) totalCount / size);

            model.addAttribute("enterList", enterList);
            model.addAttribute("page", page);
            model.addAttribute("totalPages", totalPages);

            return "enter/manage";
        }
        catch (Exception e) {
            log.error("출입 목록 조회 실패", e);
            model.addAttribute("message", "조회 중 오류가 발생했습니다.");
            return "common/error";
        }
    }


    /** 등록 폼 */
    @GetMapping("/create.do")
    public String createForm(@ModelAttribute("enter") EnterVO enter,Model model){

        try {

            if (EgovStringUtil.isEmpty(enter.getType())) {
                enter.setType("EMP");
            }

            List<UserVO> users = userService.getUserTotalList();

            model.addAttribute("userList", users);

            return "enter/create";   // 지금 쓰고 있는 템플릿 이름
        }
        catch (Exception e) {
            log.error("계정목록 조회 실패", e);
            model.addAttribute("message", "조회 중 오류가 발생했습니다.");
            return "common/error";
        }
    }

    @PostMapping("/create.do")
    public String create(@Valid @ModelAttribute("enter") EnterVO enter,
                         BindingResult binding, Model model, RedirectAttributes rttr){
        try {
            if (enterService.getEnter(enter.getEnterId()) != null) {
                binding.rejectValue("enterId", "required", "출입번호가 중복입니다.");
            }

            // 게스트인데 시작일이 없으면 오늘로
            if ("GUEST".equals(enter.getType()) && enter.getStartGuestDt() == null) {
                enter.setStartGuestDt(LocalDate.now());
            }
            // 게스트인데 마감일이 없으면 에러 체크하고 싶으면 여기서 검증
            if ("GUEST".equals(enter.getType()) && enter.getEndGuestDt() == null) {
                binding.rejectValue("endGuestDt", "required", "게스트 마감일을 입력하세요.");
            }
            //  시작일보다 마감일이 작으면 에러
            if ("GUEST".equals(enter.getType())
                    && enter.getStartGuestDt() != null
                    && enter.getEndGuestDt() != null
                    && enter.getEndGuestDt().isBefore(enter.getStartGuestDt())) {

                binding.rejectValue("endGuestDt", "range", "마감일은 시작일 이후여야 합니다.");
            }

            if (binding.hasErrors()) {
                return "enter/create";
            }
            enterService.createEnter(enter);
            rttr.addFlashAttribute("alertMessage", "출입 등록되었습니다.");

            return "redirect:/enter/manage.do";
        }
        catch (Exception e) {
            log.error("출입 등록 실패", e);
            model.addAttribute("message", "등록 중 오류가 발생했습니다.");
            return "common/error";
        }
    }



    @GetMapping("/edit/{id}.do")
    public String editForm(@PathVariable("id") String enterId,
                           Model model) {
        try {

            // 1건 조회
            EnterVO enter = enterService.getEnter(enterId);

            model.addAttribute("enter", enter);
            model.addAttribute("mode", "view");   //  처음엔 view

            return "enter/edit";   // 아래에서 만들 템플릿
        }
        catch (Exception e) {
            log.error("출입 조회 실패", e);
            model.addAttribute("message", "조회 중 오류가 발생했습니다.");
            return "common/error";
        }
    }

    @PostMapping("/edit/{id}.do")
    public String update(@PathVariable("id") String enterId,
                         @ModelAttribute("enter") EnterVO enter,
                         BindingResult binding,
                         Model model,  RedirectAttributes rttr){

        try {

            enter.setEnterId(enterId);   // 혹시 폼에 없으면 보정

            // 게스트일 때만 기간 검증
            if ("GUEST".equals(enter.getType())
                    && enter.getStartGuestDt() != null
                    && enter.getEndGuestDt() != null
                    && enter.getEndGuestDt().isBefore(enter.getStartGuestDt())) {
                binding.rejectValue("endGuestDt", "range", "마감일은 시작일 이후여야 합니다.");
            }

            if (binding.hasErrors()) {
                // edit 화면 다시
                model.addAttribute("mode", "edit");   // 에러 시에는 edit 모드로
                return "enter/edit";
            }

            enterService.updateEnter(enter);

            rttr.addFlashAttribute("alertMessage", "출입 수정되었습니다.");

            return "redirect:/enter/manage.do";
        }
        catch (Exception e) {
            log.error("출입 변경 실패", e);
            model.addAttribute("message", "변경 중 오류가 발생했습니다.");
            return "common/error";
        }
    }

    // 실제 삭제 처리 (POST 권장)
    @PostMapping("/delete/{id}.do")
    public String delete(@PathVariable("id") String enterId,Model model,RedirectAttributes rttr) {
        try {
            enterService.deleteEnter(enterId);

            rttr.addFlashAttribute("alertMessage", "출입 삭제되었습니다.");

            return "redirect:/enter/manage.do";
        }
        catch (Exception e) {
            log.error("출입 삭제 실패", e);
            model.addAttribute("message", "삭제 중 오류가 발생했습니다.");
            return "common/error";
        }
    }


}
