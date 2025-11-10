package biz.lunch.controller;

import biz.lunch.service.LunchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList; //
import java.util.HashMap; //
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
     * 등록 처리
     */
    @PostMapping("/register.do")
    public String registerLunch(HttpServletRequest request) throws Exception {

        // 1. Service가 요구하는 Map을 수동으로 조립
        Map<String, Object> params = new HashMap<>();
        params.put("storeName", request.getParameter("storeName"));
        params.put("payerId", request.getParameter("payerId"));
        params.put("date", request.getParameter("date"));
        params.put("totalAmount", request.getParameter("totalAmount"));
        params.put("type", request.getParameter("type"));

        // 2. 참여자 리스트 수동 조립
        List<Map<String, Object>> participants = new ArrayList<>();
        String[] userIds = request.getParameterValues("participantUserIds");
        String[] amounts = request.getParameterValues("participantAmounts");

        if (userIds != null && amounts != null && userIds.length == amounts.length) {
            for (int i = 0; i < userIds.length; i++) {
                Map<String, Object> p = new HashMap<>();
                p.put("userId", userIds[i]);
                p.put("individualAmount", amounts[i]);
                participants.add(p);
            }
        }
        params.put("participants", participants); // Service가 기대하는 key

        log.info("점심/커피 등록 요청 (Form): {}", params);
        lunchService.registerLunch(params);
        return "redirect:/lunch/list.do"; // 등록 후 목록 이동
    }

    /**
     * 내역 조회
     */
    @GetMapping("/list.do")
    public String getLunchList(@RequestParam(required = false) Map<String, Object> params, Model model) throws Exception {
        Map<String, Object> result = lunchService.getLunchList(params);
        model.addAttribute("lunchList", result.get("rawList"));
        model.addAttribute("settlementList", result.get("settlementList"));
        return "lunch/list";
    }


    /**
     * 수정 처리
     */
    @PostMapping("/update.do")
    public String updateLunch(HttpServletRequest request) throws Exception {

        // 1. 컨트롤러에서 Service가 요구하는 Map을 수동으로 조립
        Map<String, Object> params = new HashMap<>();
        params.put("lunchId", request.getParameter("lunchId")); // 수정 시 lunchId 필수
        params.put("storeName", request.getParameter("storeName"));
        params.put("payerId", request.getParameter("payerId"));
        params.put("date", request.getParameter("date"));
        params.put("totalAmount", request.getParameter("totalAmount"));
        params.put("type", request.getParameter("type"));

        // 2. 참여자 리스트 수동 조립
        List<Map<String, Object>> participants = new ArrayList<>();
        String[] userIds = request.getParameterValues("participantUserIds");
        String[] amounts = request.getParameterValues("participantAmounts");

        if (userIds != null && amounts != null && userIds.length == amounts.length) {
            for (int i = 0; i < userIds.length; i++) {
                Map<String, Object> p = new HashMap<>();
                p.put("userId", userIds[i]);
                p.put("individualAmount", amounts[i]);
                participants.add(p);
            }
        }
        params.put("participants", participants); // Service가 기대하는 key

        log.info("점심/커피 수정 요청 (Form): {}", params);
        lunchService.updateLunch(params);
        return "redirect:/lunch/list.do";
    }

    /**
     * 삭제 처리
     */
    @PostMapping("/delete.do")
    public String deleteLunch(@RequestParam("lunchId") int lunchId) throws Exception {
        log.info("점심/커피 삭제 요청: lunch_id={}", lunchId);
        lunchService.deleteLunch(lunchId);
        return "redirect:/lunch/list.do";
    }

    /**
     * 통계 화면
     */
    @GetMapping("/statistics.do")
    public String getStatistics(@RequestParam(required = false) Map<String, Object> params, Model model) throws Exception {
        log.info("점심/커피 통계 조회: {}", params);
        List<Map<String, Object>> stats = lunchService.getStatistics(params);
        model.addAttribute("statistics", stats);
        return "lunch/statistics"; // 통계 화면
    }

    /**
     * 정산 완료 처리
     */
    @PostMapping("/completeSettlement.do")
    public String completeSettlement(@RequestParam Map<String, Object> params) throws Exception {
        log.info("정산 완료 처리 요청: {}", params);
        lunchService.completeSettlement(params);
        return "redirect:/lunch/list.do";
    }
}