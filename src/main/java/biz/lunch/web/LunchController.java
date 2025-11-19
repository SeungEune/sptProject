package biz.lunch.web;

import biz.lunch.component.LunchViewProcessor;
import biz.lunch.service.LunchService;
import biz.lunch.vo.LunchVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 점심/커피 정산 관련 요청을 처리하는 컨트롤러 클래스
 * @author GUNWOO
 * @since 2025.11.19
 * @version 1.0

 */
@Slf4j
@Controller
@RequestMapping("/lunch")
public class LunchController {

    @Resource(name = "lunchService")
    private LunchService lunchService;

    @Resource(name = "lunchViewProcessor")
    private LunchViewProcessor viewProcessor;

    // [공통 메서드] 날짜 기본값 설정 (중복 제거)
    private void setDefaultDate(LunchVO searchVO) {
        if (searchVO.getDate() == null || searchVO.getDate().trim().isEmpty()) {
            searchVO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
    }

    /**
     * 점심/커피 등록 화면을 조회한다.
     * @param model 화면 모델
     * @return "lunch/register"
     */
    @GetMapping("/register.do")
    public String registerLunch(Model model) {
        try {
            List<UserVO> userList = lunchService.getUserList();
            model.addAttribute("userList", userList);
            return "lunch/register";
        } catch (Exception e) {
            log.error("점심/커피 등록 화면 조회 실패", e);
            // 에러 발생 시 처리 (보통 공통 에러 페이지로 이동하거나 메시지 전달)
            model.addAttribute("message", "조회 중 오류가 발생했습니다.");
            return "common/error";
        }
    }

    /**
     * 점심/커피 내역을 등록한다.
     * @param lunchVO 등록할 점심/커피 정보
     * @param model 화면 모델
     * @return "redirect:/lunch/list.do"
     */
    @PostMapping("/register.do")
    public String registerLunch(@ModelAttribute LunchVO lunchVO, Model model) {
        try {
            // 데이터 가공
            lunchVO.makeParticipantList();

            // 유효성 검사 (StoreName 체크 예시)
            if (lunchVO.getStoreName() == null || lunchVO.getStoreName().isEmpty()) {
                log.warn("가게 이름 누락");
                return "redirect:/lunch/register.do?error=storeName";
            }

            log.info("점심/커피 등록 요청: {}", lunchVO);
            lunchService.registerLunch(lunchVO);

            return "redirect:/lunch/list.do";
        } catch (Exception e) {
            log.error("점심/커피 등록 처리 실패", e);
            model.addAttribute("message", "등록 중 오류가 발생했습니다.");
            return "common/error";
        }
    }

    /**
     * 점심/커피 내역 조회 화면을 호출한다.
     * @param searchVO 검색 조건
     * @param model 화면 모델
     * @return "lunch/list"
     */
    @GetMapping("/list.do")
    public String getLunchList(@ModelAttribute LunchVO searchVO, Model model) {
        try {
            setDefaultDate(searchVO);

            // DB 데이터 조회
            List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
            List<SummaryVO> summaryList = lunchService.getStatistics(searchVO.getDate());

            // ViewProcessor 사용 (데이터 가공)
            List<Map<String, Object>> flatLunchList = viewProcessor.convertToFlatList(lunchList);

            // View 전달
            model.addAttribute("lunchList", lunchList);
            model.addAttribute("flatLunchList", flatLunchList);
            model.addAttribute("summaryList", summaryList);
            model.addAttribute("lunchVO", searchVO);

            return "lunch/list";
        } catch (Exception e) {
            log.error("점심/커피 목록 조회 실패", e);
            model.addAttribute("message", "목록 조회 중 오류가 발생했습니다.");
            return "common/error";
        }
    }

    /**
     * 점심/커피 수정 화면을 조회한다.
     * @param searchVO 검색 조건
     * @param model 화면 모델
     * @return "lunch/update"
     */
    @GetMapping("/update.do")
    public String updateLunch(@ModelAttribute LunchVO searchVO, Model model) {
        try {
            List<UserVO> userList = lunchService.getUserList();
            List<LunchVO> lunchList = lunchService.getLunchList(searchVO); // 단건 조회 권장하나 기존 로직 유지

            if (lunchList != null && !lunchList.isEmpty()) {
                model.addAttribute("lunch", lunchList.get(0));
            }
            model.addAttribute("userList", userList);
            return "lunch/update";
        } catch (Exception e) {
            log.error("수정 화면 조회 실패", e);
            return "common/error";
        }
    }

    /**
     * 점심/커피 내역을 수정한다.
     * @param lunchVO 수정할 점심/커피 정보
     * @param model 화면 모델
     * @return "redirect:/lunch/list.do"
     */
    @PostMapping("/update.do")
    public String updateLunchAction(@ModelAttribute LunchVO lunchVO, Model model) {
        try {
            lunchVO.makeParticipantList();
            lunchService.updateLunch(lunchVO);
            return "redirect:/lunch/list.do";
        } catch (Exception e) {
            log.error("점심/커피 수정 처리 실패", e);
            return "common/error";
        }
    }

    /**
     * 점심/커피 삭제 화면을 조회한다.
     * @param searchVO 검색 조건
     * @param model 화면 모델
     * @return "lunch/delete"
     */
    @GetMapping("/delete.do")
    public String deleteLunch(@ModelAttribute LunchVO searchVO, Model model) {
        try {
            List<UserVO> userList = lunchService.getUserList();
            List<LunchVO> lunchList = lunchService.getLunchList(searchVO);

            if (lunchList != null && !lunchList.isEmpty()) {
                model.addAttribute("lunch", lunchList.get(0));
            }
            model.addAttribute("userList", userList);
            return "lunch/delete";
        } catch (Exception e) {
            log.error("삭제 화면 조회 실패", e);
            return "common/error";
        }
    }

    /**
     * 점심/커피 내역을 삭제한다.
     * @param lunchVO 삭제할 점심/커피 정보
     * @param model 화면 모델
     * @return "redirect:/lunch/list.do"
     */
    @PostMapping("/delete.do")
    public String deleteLunchAction(@ModelAttribute LunchVO lunchVO, Model model) {
        try {
            lunchService.deleteLunch(lunchVO.getLunchId());
            return "redirect:/lunch/list.do";
        } catch (Exception e) {
            log.error("점심/커피 삭제 처리 실패", e);
            return "common/error";
        }
    }

    /**
     * 월별 점심/커피 통계 화면을 조회한다.
     * @param searchVO 검색 조건
     * @param model 화면 모델
     * @return "lunch/statistics"
     */
    @GetMapping("/statistics.do")
    public String getStatistics(@ModelAttribute LunchVO searchVO, Model model) {
        try {
            setDefaultDate(searchVO);
            String searchMonth = searchVO.getDate();

            List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
            List<SummaryVO> summaryList = lunchService.getStatistics(searchMonth);

            model.addAttribute("lunchList", lunchList);
            model.addAttribute("summaryList", summaryList);

            return "lunch/statistics";
        } catch (Exception e) {
            log.error("통계 조회 실패", e);
            return "common/error";
        }
    }

    /**
     * 정산 완료 상태를 처리한다.
     * @param month 정산 대상 월
     * @param userId 사용자 ID
     * @param action 처리 액션 (complete or cancel)
     * @param model 화면 모델
     * @return "redirect:/lunch/list.do"
     */
    @PostMapping("/completeSettlement.do")
    public String completeSettlement(
            @RequestParam("month") String month,
            @RequestParam("userId") String userId,
            @RequestParam(required = false, defaultValue = "complete") String action,
            Model model) {
        try {
            lunchService.completeSettlement(month, userId, action);

            if (month != null && !month.isEmpty()) {
                return "redirect:/lunch/list.do?date=" + month;
            } else {
                return "redirect:/lunch/list.do";
            }
        } catch (Exception e) {
            log.error("정산 처리 실패", e);
            return "redirect:/lunch/list.do";
        }
    }
}