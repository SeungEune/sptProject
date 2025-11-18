package biz.lunch.controller;

import biz.lunch.service.LunchService;
import biz.lunch.vo.LunchVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String registerLunch(@ModelAttribute LunchVO lunchVO) throws Exception {
        // List<ParticipantVO>로 변환
        lunchVO.makeParticipantList();

        log.info("점심/커피 등록 요청: {}", lunchVO);
        lunchService.registerLunch(lunchVO);
        return "redirect:/lunch/list.do";
    }

    /**
     * 내역 조회
     * - 검색 조건도 LunchVO로 통일하여 받음
     */
    @GetMapping("/list.do")
    public String getLunchList(@ModelAttribute LunchVO searchVO, Model model) throws Exception {
        // 날짜(searchMonth) 기본값 설정
        if (searchVO.getDate() == null || searchVO.getDate().isEmpty()) {
            searchVO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        String searchMonth = searchVO.getDate();

        log.info("점심/커피 목록 조회: searchMonth={}", searchMonth);

        // 데이터 조회
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
        List<SummaryVO> summaryList = lunchService.getStatistics(searchMonth);

        // [화면 표시용 로직] 날짜 기준 그룹핑 및 RowSpan 계산 (기존 로직 유지)
        Map<String, List<LunchVO>> byDate = new LinkedHashMap<>();
        for (LunchVO item : lunchList) {
            if (item.getDate() == null) continue;
            byDate.computeIfAbsent(item.getDate(), k -> new ArrayList<>()).add(item);
        }

        List<Map<String, Object>> flatLunchList = new ArrayList<>();
        final String REPRESENTATIVE_NAME = "이승은";

        for (Map.Entry<String, List<LunchVO>> entry : byDate.entrySet()) {
            List<LunchVO> itemsForDate = entry.getValue();
            List<Map<String, Object>> dailyRows = new ArrayList<>();

            for (LunchVO item : itemsForDate) {
                String payerName = item.getPayerName() != null ? item.getPayerName() : "";
                boolean isRepresentative = REPRESENTATIVE_NAME.equals(payerName);

                // DETAIL 행
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

                // PAY 행 (대표자가 아닐 경우 추가)
                if (!isRepresentative) {
                    Map<String, Object> payRow = new HashMap<>(detailRow);
                    payRow.put("rowType", "PAY");
                    dailyRows.add(payRow);
                }
            }

            // RowSpan 처리
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
     * - lunchId를 LunchVO로 받음
     */
    @GetMapping("/update.do")
    public String updateLunch(@ModelAttribute LunchVO searchVO, Model model) throws Exception {
        log.info("점심/커피 수정 화면 요청: lunchId={}", searchVO.getLunchId());

        // 사용자 목록
        List<UserVO> userList = lunchService.getUserList();

        // 해당 데이터 조회
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);

        if (lunchList != null && !lunchList.isEmpty()) {
            model.addAttribute("lunch", lunchList.get(0));
        }
        model.addAttribute("userList", userList);
        return "lunch/update";
    }

    /**
     * 수정 처리
     */
    @PostMapping("/update.do")
    public String updateLunch(@ModelAttribute LunchVO lunchVO) throws Exception {
        // List<ParticipantVO>로 변환
        lunchVO.makeParticipantList();

        log.info("점심/커피 수정 요청: {}", lunchVO);
        lunchService.updateLunch(lunchVO);
        return "redirect:/lunch/list.do";
    }

    /**
     * 삭제 화면
     */
    @GetMapping("/delete.do")
    public String deleteLunch(@ModelAttribute LunchVO searchVO, Model model) throws Exception {
        log.info("점심/커피 삭제 화면 요청: lunchId={}", searchVO.getLunchId());

        List<UserVO> userList = lunchService.getUserList();
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);

        if (lunchList != null && !lunchList.isEmpty()) {
            model.addAttribute("lunch", lunchList.get(0));
        }

        model.addAttribute("userList", userList);
        return "lunch/delete";
    }

    /**
     * 삭제 처리
     */
    @PostMapping("/delete.do")
    public String deleteLunch(@ModelAttribute LunchVO lunchVO) throws Exception {
        log.info("점심/커피 삭제 요청: lunchId={}", lunchVO.getLunchId());
        lunchService.deleteLunch(lunchVO.getLunchId());
        return "redirect:/lunch/list.do";
    }

    /**
     * 통계 화면
     */
    @GetMapping("/statistics.do")
    public String getStatistics(@ModelAttribute LunchVO searchVO, Model model) throws Exception {
        // 날짜 기본값 설정
        if (searchVO.getDate() == null || searchVO.getDate().isEmpty()) {
            searchVO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        String searchMonth = searchVO.getDate();

        log.info("점심/커피 통계 조회: searchMonth={}", searchMonth);

        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
        List<SummaryVO> summaryList = lunchService.getStatistics(searchMonth);

        model.addAttribute("lunchList", lunchList);
        model.addAttribute("summaryList", summaryList);
        model.addAttribute("searchMonth", searchMonth);

        return "lunch/statistics";
    }

    /**
     * 정산 완료 처리(여기는 requestParam으로 받음)
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