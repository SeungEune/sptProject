package biz.lunch.controller;

import biz.lunch.component.LunchViewProcessor;
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
    private final LunchViewProcessor viewProcessor;
    /**
     * 점심/커피 등록 화면을 조회한다.
     *
     * @param model 화면 전달용 Model
     * @return lunch/register 등록 화면
     * @throws Exception 시스템 예외
     */
    @GetMapping("/register.do")
    public String registerLunch(Model model) throws Exception {
        List<UserVO> userList = lunchService.getUserList();
        model.addAttribute("userList", userList);
        return "lunch/register";
    }

    /**
     * 점심/커피 내역을 등록한다.
     *
     * @param lunchVO 등록할 점심/커피 정보 VO
     * @return 목록 화면으로 Redirect
     * @throws Exception 시스템 예외
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
     * 점심/커피 내역 조회 화면을 호출한다.
     *
     * @param searchVO 검색 조건(월, 사용자 등)
     * @param model 화면 전달용 Model
     * @return lunch/list 목록 화면
     * @throws Exception 시스템 예외
     */
    @GetMapping("/list.do")
    public String getLunchList(@ModelAttribute LunchVO searchVO, Model model) throws Exception {

        // 날짜 기본값 설정
        if (searchVO.getDate() == null || searchVO.getDate().isEmpty()) {
            searchVO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        // DB 데이터 조회
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
        List<SummaryVO> summaryList = lunchService.getStatistics(searchVO.getDate());
        List<Map<String, Object>> flatLunchList = viewProcessor.convertToFlatList(lunchList);

        // View 전달
        model.addAttribute("lunchList", lunchList);       // 원본 데이터
        model.addAttribute("flatLunchList", flatLunchList); // 화면에 뿌릴 가공된 데이터
        model.addAttribute("summaryList", summaryList);
        model.addAttribute("lunchVO", searchVO);          // 검색 조건 유지

        return "lunch/list";
    }

    /**
     * 수정 화면을 조회한다.
     *
     * @param searchVO 수정하려는 lunchId가 담긴 VO
     * @param model 모델 객체
     * @return lunch/update 수정 화면
     * @throws Exception 시스템 예외
     */
    @GetMapping("/update.do")
    public String updateLunch(@ModelAttribute LunchVO searchVO, Model model) throws Exception {
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
     * 점심/커피 내역을 수정한다.
     *
     * @param lunchVO 수정된 LunchVO
     * @return 목록 화면 Redirect
     * @throws Exception 시스템 예외
     */
    @PostMapping("/update.do")
    public String updateLunch(@ModelAttribute LunchVO lunchVO) throws Exception {
        // List<ParticipantVO>로 변환
        lunchVO.makeParticipantList();
        lunchService.updateLunch(lunchVO);
        return "redirect:/lunch/list.do";
    }

    /**
     * 삭제 화면을 조회한다.
     *
     * @param searchVO 삭제할 lunchId가 담긴 VO
     * @param model 화면 전달 모델
     * @return lunch/delete 삭제 화면
     * @throws Exception 시스템 예외
     */
    @GetMapping("/delete.do")
    public String deleteLunch(@ModelAttribute LunchVO searchVO, Model model) throws Exception {

        List<UserVO> userList = lunchService.getUserList();
        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);

        if (lunchList != null && !lunchList.isEmpty()) {
            model.addAttribute("lunch", lunchList.get(0));
        }
        model.addAttribute("userList", userList);
        return "lunch/delete";
    }

    /**
     * 점심/커피 내역을 삭제한다.
     *
     * @param lunchVO 삭제할 lunchId를 담은 VO
     * @return 목록 화면 Redirect
     * @throws Exception 시스템 예외
     */
    @PostMapping("/delete.do")
    public String deleteLunch(@ModelAttribute LunchVO lunchVO) throws Exception {
        lunchService.deleteLunch(lunchVO.getLunchId());
        return "redirect:/lunch/list.do";
    }

    /**
     * 월별 점심/커피 통계를 조회한다.
     *
     * @param searchVO 검색 조건(월)
     * @param model 화면 전달용 Model
     * @return lunch/statistics 통계 화면
     * @throws Exception 시스템 예외
     */
    @GetMapping("/statistics.do")
    public String getStatistics(@ModelAttribute LunchVO searchVO, Model model) throws Exception {
        // 날짜 기본값 설정
        if (searchVO.getDate() == null || searchVO.getDate().isEmpty()) {
            searchVO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }
        String searchMonth = searchVO.getDate();

        List<LunchVO> lunchList = lunchService.getLunchList(searchVO);
        List<SummaryVO> summaryList = lunchService.getStatistics(searchMonth);

        model.addAttribute("lunchList", lunchList);
        model.addAttribute("summaryList", summaryList);

        return "lunch/statistics";
    }

    /**
     * 정산 완료 상태를 처리한다.
     *
     * @param month 대상 월(yyyy-MM)
     * @param userId 정산 완료 처리할 사용자 ID
     * @param action 처리 액션(기본값: complete)
     * @return 목록 화면 Redirect
     * @throws Exception 시스템 예외
     */
    @PostMapping("/completeSettlement.do")
    public String completeSettlement(
            @RequestParam("month") String month,
            @RequestParam("userId") String userId,
            @RequestParam(required = false, defaultValue = "complete") String action) throws Exception {

        lunchService.completeSettlement(month, userId, action);

        if (month != null && !month.isEmpty()) {
            return "redirect:/lunch/list.do?date=" + month;
        } else {
            return "redirect:/lunch/list.do";
        }
    }
}