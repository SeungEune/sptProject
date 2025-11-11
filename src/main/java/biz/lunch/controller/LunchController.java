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
    @GetMapping("/register.do")
    public String registerLunch(Model model) throws Exception {
        // 화면에 뿌릴 사용자 목록 (계산자/참석자 선택용)
        List<Map<String, Object>> userList = lunchService.getUserList();
        model.addAttribute("userList", userList);

        return "lunch/register";
    }

    @PostMapping("/register.do")
    public String registerLunch(HttpServletRequest request) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("storeName", request.getParameter("storeName"));
        params.put("payerId", request.getParameter("payerId"));
        params.put("date", request.getParameter("date"));
        params.put("type", request.getParameter("type"));

        // 참여자 리스트
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
        return "redirect:/lunch/list.do"; //
    }

    /**
     * 내역 조회
     */
    @GetMapping("/list.do")
    public String getLunchList(@RequestParam(required = false) Map<String, Object> params, Model model) throws Exception {
        log.info("점심/커피 목록 조회: {}", params);

        List<Map<String, Object>> lunchList = lunchService.getLunchList(params);
        List<Map<String, Object>> summaryList = lunchService.getStatistics(params);

        // 데이터를 Model에 추가
        model.addAttribute("lunchList", lunchList);   // 상단 목록
        model.addAttribute("summaryList", summaryList); // 하단 요약
        model.addAttribute("params", params);       // 검색 조건 유지를 위해

        return "lunch/list";
    }


    /**
     * 수정 처리
     */
    @PostMapping("/update.do")
    public String updateLunch(HttpServletRequest request) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("lunchId", request.getParameter("lunchId")); // 수정 시 lunchId 필수
        params.put("storeName", request.getParameter("storeName"));
        params.put("payerId", request.getParameter("payerId"));
        params.put("date", request.getParameter("date"));
        params.put("type", request.getParameter("type"));

        // 참여자 리스트
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
        // Service가 기대하는 key
        params.put("participants", participants);

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

        List<Map<String, Object>> lunchList = lunchService.getLunchList(params);
        List<Map<String, Object>> summaryList = lunchService.getStatistics(params);

        // 데이터를 Model에 추가
        model.addAttribute("lunchList", lunchList); // 일자별 지출 그래프
        model.addAttribute("summaryList", summaryList); // 사용자별 통계 그래프
        model.addAttribute("params", params);

        return "lunch/statistics";
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