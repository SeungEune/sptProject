package biz.lunch.web;

import biz.lunch.component.LunchViewProcessor;
import biz.lunch.service.LunchService;
import biz.lunch.vo.LunchVO;
import biz.lunch.vo.SummaryVO;
import biz.lunch.vo.UserVO;
import biz.util.EgovDateUtil;
import biz.util.EgovStringUtil;
import egovframework.com.cmm.EgovMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
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
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;
    // 날짜 기본값 설정
    private void setDefaultDate(LunchVO searchVO) {
        if (EgovStringUtil.isEmpty(searchVO.getDate())) {
            String today = EgovDateUtil.getToday();
            // 파라미터: (날짜문자열, 시간(안쓰니까 "0000"), 변환할포맷
            String yyyyMM = EgovDateUtil.convertDate(today, "0000", "yyyy-MM");
            searchVO.setDate(yyyyMM);
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
            // 에러 발생 시 처리 (공통 에러 페이지로 이동하거나 메시지 전달)
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.msg"));
            return "error/404";
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

            log.info("점심/커피 등록 요청: {}", lunchVO);
            lunchService.registerLunch(lunchVO);
            return "redirect:/lunch/list.do";
        } catch (IllegalArgumentException e) {
            // VO에서 던진 "금액 누락 에러를 잡는 곳
            log.warn("잘못된 데이터 요청: {}", e.getMessage());
            return "redirect:/lunch/register.do?error=amount";
        } catch (Exception e) {
            log.error("점심/커피 등록 처리 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.insert"));
            return "error/404";
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
            List<Map<String, Object>> flatLunchList;
            if (lunchList != null) {
                flatLunchList = viewProcessor.convertToFlatList(lunchList);
            } else {
                flatLunchList = Collections.emptyList();
            }
            // View 전달
            model.addAttribute("lunchList", lunchList);
            model.addAttribute("flatLunchList", flatLunchList);
            model.addAttribute("summaryList", summaryList);
            model.addAttribute("lunchVO", searchVO);
            return "lunch/list";

        } catch (Exception e) {
            log.error("점심/커피 목록 조회 실패", e);
            model.addAttribute("message", egovMessageSource.getMessage("fail.common.select"));
            return "error/404";
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
            return "error/404";
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

        } catch (IllegalArgumentException e) {
            // 수정 중 금액 누락 발생 시 수정 화면으로 리다이렉트
            return "redirect:/lunch/update.do?error=amount";

        } catch (Exception e) {
            log.error("점심/커피 수정 처리 실패", e);
            return "error/404";
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
            return "error/404";
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
            return "error/404";
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
            return "error/404";
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