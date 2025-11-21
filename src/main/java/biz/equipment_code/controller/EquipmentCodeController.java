package biz.equipment_code.controller;

import biz.equipment_code.dto.EquipmentCodeRequest;
import biz.equipment_code.dto.EquipmentCodeResponse;
import biz.equipment_code.dto.EquipmentCodeUpdate;
import biz.equipment_code.service.EquipmentCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/equipmentCode")
@RequiredArgsConstructor
public class EquipmentCodeController {

    final private EquipmentCodeService equipmentCodeService;

    // 장비 분류 조회
    @GetMapping("/list.do")
    public String getEquipmentCodes(Model model) {
        List<EquipmentCodeResponse> codes = equipmentCodeService.getCodes();
        model.addAttribute("equipmentCode", codes);
        return "equipmentCode/list";
    }

    // 장비 분류 단건 조회
    @GetMapping("/view.do")
    public String getEquipmentCode(@RequestParam("codeId") Long codeId, Model model) {
        EquipmentCodeResponse code = equipmentCodeService.getEquipmentCode(codeId);
        model.addAttribute("equipmentCode", code);
        return "equipmentCode/view";
    }

    @GetMapping("/insert.do")
    public String insertForm() {
        return "equipmentCode/insert";
    }

    // 장비 분류 등록
    @PostMapping("/insert.do")
    public String insertEquipmentCode(@ModelAttribute EquipmentCodeRequest request) {
        equipmentCodeService.insertCode(request);
        return "redirect:/equipmentCode/list.do";
    }

    // 장비 분류 수정
    @GetMapping("/update.do")
    public String updateForm(@RequestParam("codeId") Long codeId, Model model) {
        EquipmentCodeResponse code = equipmentCodeService.getEquipmentCode(codeId);
        model.addAttribute("EquipmentCode", code);
        return "equipmentCode/update";
    }

    @PostMapping("/update.do")
    public String updateEquipmentCode(@ModelAttribute EquipmentCodeUpdate update) {
        equipmentCodeService.updateCode(update);
        return "redirect:/equipmentCode/list.do";
    }

    // 장비 분류 삭제
    @DeleteMapping("/delete.do")
    public String deleteEquipmentCode(@RequestParam("codeId") Long codeId) {
        equipmentCodeService.deleteCode(codeId);
        return "redirect:/equipmentCode/list.do";
    }

    // 장비 필터 조회
    @GetMapping("/filterView.do")
    public String getFilteredCode(Model model, @RequestParam("names") List<String> names) {
        model.addAttribute("equipmentCode",equipmentCodeService.getFilteredCode(names));
        return "equipmentCode/filterView";
    }
}
