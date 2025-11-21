package biz.eqp_history.web;

import biz.eqp_history.service.EqpHistoryService;
import biz.eqp_history.vo.EqpHistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@RequiredArgsConstructor
@RequestMapping("/eqp-history")
@Controller
public class EqpHistoryController {

    @Resource(name = "eqpHistoryServiceImpl")
    private final EqpHistoryService eqpHistoryService;

    @PostMapping("/insert.do")
    public void putEqpHistory(EqpHistoryVO eqpHistoryVO) {
        eqpHistoryService.insertEqpHistoryVO(eqpHistoryVO);
    }

    @GetMapping("/view.do")
    public String getEqpHistory(Long epqId, Model model) {
         model.addAttribute(eqpHistoryService.getEqpHistoryByEqpId(epqId));
         return "/eqp-history/view";
    }
}
