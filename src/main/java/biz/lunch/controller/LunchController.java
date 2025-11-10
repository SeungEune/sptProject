package biz.lunch.controller;

import biz.lunch.service.LunchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 점심/커피 정산 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lunch")
public class LunchController {

    private final LunchService lunchService;

    /**
     *  등록 처리
     */

    @PostMapping("/register.do")
    public String registerLunch(@RequestParam Map<String, Object> params) throws Exception {
        log.info("점심/커피 등록 요청: {}", params);
        lunchService.registerLunch(params);
        return "redirect:/lunch/list.do"; // 등록 후 목록 이동
    }

    /**
     *  내역 조회 / 수정 / 삭제
     */
    @GetMapping("/list.do")
    public String getLunchList(@RequestParam(required = false) Map<String, Object> params, Model model) throws Exception {
        log.info("점심/커피 목록 조회: {}", params);
        List<Map<String, Object>> list = lunchService.getLunchList(params);
        model.addAttribute("lunchList", list);
        return "lunch/list";
    }

    @PostMapping("/update.do")
    public String updateLunch(@RequestParam Map<String, Object> params) throws Exception {
        log.info("점심/커피 수정 요청: {}", params);
        lunchService.updateLunch(params);
        return "redirect:/lunch/list.do";
    }

    @PostMapping("/delete.do")
    public String deleteLunch(@RequestParam("lunch_id") int lunchId) throws Exception {
        log.info("점심/커피 삭제 요청: lunch_id={}", lunchId);
        lunchService.deleteLunch(lunchId);
        return "redirect:/lunch/list.do";
    }

    /**
     *  통계 화면
     *  */
    @GetMapping("/statistics.do")
    public String getStatistics(@RequestParam(required = false) Map<String, Object> params, Model model) throws Exception {
        log.info("점심/커피 통계 조회: {}", params);
        Map<String, Object> stats = lunchService.getStatistics(params);
        model.addAttribute("statistics", stats);
        return "lunch/statistics"; // 통계 화면
    }

    /**
     *  정산 완료 처리
     *  */
    @PostMapping("/completeSettlement.do")
    public String completeSettlement(@RequestParam Map<String, Object> params) throws Exception {
        log.info("정산 완료 처리 요청: {}", params);
        lunchService.completeSettlement(params);
        return "redirect:/lunch/list.do";
    }
}
