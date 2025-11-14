package biz.lunch.controller;

import biz.lunch.service.LunchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
     * 등록 화면
     */
    @GetMapping("/register.do")
    public String registerLunch(Model model) throws Exception {
        // 화면에 뿌릴 사용자 목록 (계산자/참석자 선택용)
        List<Map<String, Object>> userList = lunchService.getUserList();
        model.addAttribute("userList", userList);

        return "lunch/register";
    }

    /**
     * 등록 처리
     */
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
        return "redirect:/lunch/list.do";
    }

    /**
     * 내역 조회
     */
    @GetMapping("/list.do")
    public String getLunchList(@RequestParam(required = false) Map<String, Object> params, Model model) throws Exception {
        log.info("점심/커피 목록 조회: {}", params);

        // 1. searchMonth 기본값 설정
        if (params.get("searchMonth") == null || params.get("searchMonth").toString().isEmpty()) {
            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            params.put("searchMonth", currentMonth);
        }

        // 2. 통계 조회용 month 세팅
        if (params.containsKey("searchMonth")) {
            params.put("month", params.get("searchMonth"));
        }

        // 3. 원본 데이터 조회
        List<Map<String, Object>> lunchList = lunchService.getLunchList(params);
        List<Map<String, Object>> summaryList = lunchService.getStatistics(params);

        // 4. 날짜 기준으로 그룹핑
        Map<String, List<Map<String, Object>>> byDate = new LinkedHashMap<>();
        for (Map<String, Object> item : lunchList) {
            Object dateObj = item.get("date");
            if (dateObj == null) continue;
            String date = dateObj.toString();
            byDate.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
        }

        // 5. 화면용 flat 리스트 생성 (DETAIL / PAY)
        List<Map<String, Object>> flatLunchList = new ArrayList<>();
        final String REPRESENTATIVE_NAME = "이승은"; // 기본 정산자

        for (Map.Entry<String, List<Map<String, Object>>> entry : byDate.entrySet()) {
            String date = entry.getKey();
            List<Map<String, Object>> itemsForDate = entry.getValue();

            List<Map<String, Object>> dailyRows = new ArrayList<>();

            for (Map<String, Object> item : itemsForDate) {
                String payerName = item.get("payer_name") != null ? item.get("payer_name").toString() : "";
                boolean isRepresentative = REPRESENTATIVE_NAME.equals(payerName);

                //  DETAIL 행 (참여자 금액 행)
                Map<String, Object> detailRow = new HashMap<>(item);
                detailRow.put("rowType", "DETAIL");
                dailyRows.add(detailRow);

                // PAY 행 (결제 금액 행) - 대표 정산자가 아닐 때만
                if (!isRepresentative) {
                    Map<String, Object> payRow = new HashMap<>(item);
                    payRow.put("rowType", "PAY");
                    dailyRows.add(payRow);
                }
            }

            // 날짜 셀 rowspan 계산 & 첫 행 표시 플래그
            int rowSpan = dailyRows.size();
            for (int i = 0; i < dailyRows.size(); i++) {
                Map<String, Object> row = dailyRows.get(i);
                row.put("isFirstOfDate", i == 0);   // 첫 행이면 true
                if (i == 0) {
                    row.put("dateRowSpan", rowSpan); // 첫 행에만 rowspan 값
                }
                flatLunchList.add(row);
            }
        }

        // 6. 모델에 담기
        model.addAttribute("lunchList", lunchList);         // 원본 (필요 시 사용)
        model.addAttribute("flatLunchList", flatLunchList); // 화면 표시용
        model.addAttribute("summaryList", summaryList);
        model.addAttribute("params", params);

        return "lunch/list";
    }

    /**
     * 수정 화면
     */
    @GetMapping("/update.do")
    public String updateLunch(@RequestParam("lunchId") int lunchId, Model model) throws Exception {
        log.info("점심/커피 수정 화면 요청: lunchId={}", lunchId);
        
        // 사용자 목록 조회
        List<Map<String, Object>> userList = lunchService.getUserList();
        
        // 해당 점심 데이터 조회
        Map<String, Object> params = new HashMap<>();
        params.put("lunchId", lunchId);
        List<Map<String, Object>> lunchList = lunchService.getLunchList(params);
        
        if (lunchList != null && !lunchList.isEmpty()) {
            Map<String, Object> lunch = lunchList.get(0);
            model.addAttribute("lunch", lunch);
        }
        
        model.addAttribute("userList", userList);
        return "lunch/update";
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
     * 삭제 화면
     */
    @GetMapping("/delete.do")
    public String deleteLunch(@RequestParam("lunchId") int lunchId, Model model) throws Exception {
        log.info("점심/커피 삭제 화면 요청: lunchId={}", lunchId);
        
        // 사용자 목록 조회
        List<Map<String, Object>> userList = lunchService.getUserList();
        
        // 해당 점심 데이터 조회
        Map<String, Object> params = new HashMap<>();
        params.put("lunchId", lunchId);
        List<Map<String, Object>> lunchList = lunchService.getLunchList(params);
        
        if (lunchList != null && !lunchList.isEmpty()) {
            Map<String, Object> lunch = lunchList.get(0);
            model.addAttribute("lunch", lunch);
        }
        
        model.addAttribute("userList", userList);
        return "lunch/delete";
    }

    /**
     * 삭제 처리
     */
    @PostMapping("/delete.do")
    public String deleteLunch(@RequestParam("lunchId") int lunchId) throws Exception {
        log.info("점심/커피 삭제 요청: lunchId={}", lunchId);
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

        log.info("정산 완료/취소 통합 처리 요청: {}", params);
        lunchService.completeSettlement(params);
        // 2. 리다이렉트 로직은 동일
        String month = (String) params.get("month");
        if (month != null && !month.isEmpty()) {
            return "redirect:/lunch/list.do?searchMonth=" + month;
        } else {
            return "redirect:/lunch/list.do";
        }
    }
}