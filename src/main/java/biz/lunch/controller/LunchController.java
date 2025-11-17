package biz.lunch.controller;

import biz.lunch.service.LunchService;
import biz.lunch.vo.LunchVO;
import biz.lunch.vo.ParticipantVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        List<UserVO> userList = lunchService.getUserList();
        model.addAttribute("userList", userList);
        return "lunch/register";
    }

    /**
     * 등록 처리
     */
    @PostMapping("/register.do")
    public String registerLunch(HttpServletRequest request) throws Exception {
        // LunchVO 생성
        LunchVO lunchVO = LunchVO.builder()
                .date(request.getParameter("date"))
                .storeName(request.getParameter("storeName"))
                .payerId(request.getParameter("payerId"))
                .type(request.getParameter("type"))
                .build();

        // 참여자 리스트 생성
        String[] userIds = request.getParameterValues("participantUserIds");
        String[] amounts = request.getParameterValues("participantAmounts");
        
        List<ParticipantVO> participantList = new ArrayList<>();
        if (userIds != null && amounts != null && userIds.length == amounts.length) {
            for (int i = 0; i < userIds.length; i++) {
                ParticipantVO participant = ParticipantVO.builder()
                        .userId(userIds[i])
                        .individualAmount(Integer.parseInt(amounts[i]))
                        .build();
                participantList.add(participant);
            }
        }
        lunchVO.setParticipantList(participantList);

        log.info("점심/커피 등록 요청: {}", lunchVO);
        lunchService.registerLunch(lunchVO);
        return "redirect:/lunch/list.do";
    }

    /**
     * 내역 조회
     */
    @GetMapping("/list.do")
    public String getLunchList(@RequestParam(required = false, defaultValue = "") String searchMonth, Model model) throws Exception {
        log.info("점심/커피 목록 조회: searchMonth={}", searchMonth);

        // searchMonth 기본값 설정
        if (searchMonth == null || searchMonth.isEmpty()) {
            searchMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // LunchVO 검색 조건 생성
        LunchVO searchVO = LunchVO.builder()
                .date(searchMonth)  // ServiceImpl에서 startDate~endDate로 변환
                .build();

        // 데이터 조회
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
        List<SummaryVO> summaryList = lunchService.getStatistics(searchMonth);

        // 날짜 기준으로 그룹핑 (기존 로직 유지)
        Map<String, List<LunchVO>> byDate = new LinkedHashMap<>();
        for (LunchVO item : lunchList) {
            if (item.getDate() == null) continue;
            String date = item.getDate();
            byDate.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
        }

        // 화면용 flat 리스트 생성 (DETAIL / PAY)
        List<Map<String, Object>> flatLunchList = new ArrayList<>();
        final String REPRESENTATIVE_NAME = "이승은";

        for (Map.Entry<String, List<LunchVO>> entry : byDate.entrySet()) {
            String date = entry.getKey();
            List<LunchVO> itemsForDate = entry.getValue();

            List<Map<String, Object>> dailyRows = new ArrayList<>();

            for (LunchVO item : itemsForDate) {
                String payerName = item.getPayerName() != null ? item.getPayerName() : "";
                boolean isRepresentative = REPRESENTATIVE_NAME.equals(payerName);

                // DETAIL 행 (참여자 금액 행)
                Map<String, Object> detailRow = new HashMap<>();
                detailRow.put("lunchId", item.getLunchId());
                detailRow.put("date", item.getDate());
                detailRow.put("storeName", item.getStoreName());
                detailRow.put("totalAmount", item.getTotalAmount());
                detailRow.put("type", item.getType());
                detailRow.put("payerId", item.getPayerId());
                detailRow.put("payerName", item.getPayerName());
                detailRow.put("participants", item.getParticipants());
                detailRow.put("rowType", "DETAIL");
                dailyRows.add(detailRow);

                // PAY 행 (결제 금액 행) - 대표 정산자가 아닐 때만
                if (!isRepresentative) {
                    Map<String, Object> payRow = new HashMap<>(detailRow);
                    payRow.put("rowType", "PAY");
                    dailyRows.add(payRow);
                }
            }

            // 날짜 셀 rowspan 계산 & 첫 행 표시 플래그
            int rowSpan = dailyRows.size();
            for (int i = 0; i < dailyRows.size(); i++) {
                Map<String, Object> row = dailyRows.get(i);
                row.put("isFirstOfDate", i == 0);
                if (i == 0) {
                    row.put("dateRowSpan", rowSpan);
                }
                flatLunchList.add(row);
            }
        }

        model.addAttribute("lunchList", lunchList);
        model.addAttribute("flatLunchList", flatLunchList);
        model.addAttribute("summaryList", summaryList);
        model.addAttribute("searchMonth", searchMonth);

        return "lunch/list";
    }

    /**
     * 수정 화면
     */
    @GetMapping("/update.do")
    public String updateLunch(@RequestParam("lunchId") int lunchId, Model model) throws Exception {
        log.info("점심/커피 수정 화면 요청: lunchId={}", lunchId);

        // 사용자 목록 조회
        List<UserVO> userList = lunchService.getUserList();

        // 해당 점심 데이터 조회
        LunchVO searchVO = LunchVO.builder().lunchId(lunchId).build();
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);

        if (lunchList != null && !lunchList.isEmpty()) {
            LunchVO lunch = lunchList.get(0);
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
        // LunchVO 생성
        LunchVO lunchVO = LunchVO.builder()
                .lunchId(Integer.parseInt(request.getParameter("lunchId")))
                .date(request.getParameter("date"))
                .storeName(request.getParameter("storeName"))
                .payerId(request.getParameter("payerId"))
                .type(request.getParameter("type"))
                .build();

        // 참여자 리스트 생성
        String[] userIds = request.getParameterValues("participantUserIds");
        String[] amounts = request.getParameterValues("participantAmounts");

        List<ParticipantVO> participantList = new ArrayList<>();
        if (userIds != null && amounts != null && userIds.length == amounts.length) {
            for (int i = 0; i < userIds.length; i++) {
                ParticipantVO participant = ParticipantVO.builder()
                        .userId(userIds[i])
                        .individualAmount(Integer.parseInt(amounts[i]))
                        .build();
                participantList.add(participant);
            }
        }
        lunchVO.setParticipantList(participantList);

        log.info("점심/커피 수정 요청: {}", lunchVO);
        lunchService.updateLunch(lunchVO);
        return "redirect:/lunch/list.do";
    }

    /**
     * 삭제 화면
     */
    @GetMapping("/delete.do")
    public String deleteLunch(@RequestParam("lunchId") int lunchId, Model model) throws Exception {
        log.info("점심/커피 삭제 화면 요청: lunchId={}", lunchId);

        // 사용자 목록 조회
        List<UserVO> userList = lunchService.getUserList();

        // 해당 점심 데이터 조회
        LunchVO searchVO = LunchVO.builder().lunchId(lunchId).build();
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);

        if (lunchList != null && !lunchList.isEmpty()) {
            LunchVO lunch = lunchList.get(0);
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
    public String getStatistics(@RequestParam(required = false, defaultValue = "") String searchMonth, Model model) throws Exception {
        log.info("점심/커피 통계 조회: searchMonth={}", searchMonth);

        // searchMonth 기본값 설정
        if (searchMonth == null || searchMonth.isEmpty()) {
            searchMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // LunchVO 검색 조건 생성 (통계는 month만 필요)
        LunchVO searchVO = LunchVO.builder()
                .date(searchMonth)
                .build();

        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
        List<SummaryVO> summaryList = lunchService.getStatistics(searchMonth);

        model.addAttribute("lunchList", lunchList);
        model.addAttribute("summaryList", summaryList);
        model.addAttribute("searchMonth", searchMonth);

        return "lunch/statistics";
    }

    /**
     * 정산 완료 처리
     */
    @PostMapping("/completeSettlement.do")
    public String completeSettlement(
            @RequestParam("month") String month,
            @RequestParam("userId") String userId,
            @RequestParam(required = false, defaultValue = "complete") String action) throws Exception {

        log.info("정산 완료/취소 통합 처리 요청: month={}, userId={}, action={}", month, userId, action);
        
        lunchService.completeSettlement(month, userId, action);

        if (month != null && !month.isEmpty()) {
            return "redirect:/lunch/list.do?searchMonth=" + month;
        } else {
            return "redirect:/lunch/list.do";
        }
    }
}
