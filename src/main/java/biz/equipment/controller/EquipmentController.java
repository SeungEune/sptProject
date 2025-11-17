package biz.equipment.controller;

import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.service.EquipmentService;
import biz.equipment.service.EquipmentServiceImpl;
import biz.equipment.vo.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {

    final private EquipmentService equipmentService;
    // 장비 조회
    @GetMapping("/list.do")
    public String getEquipmentCodes(Model model) {
        model.addAttribute("equipment", equipmentService.getEquipments()) ;
        return "equipment/list";
    }

    // 장비 단건 조회
    @GetMapping("/view.do")
    public String getEquipmentCode(@RequestParam("id") Long id, Model model) {
        model.addAttribute(equipmentService.getEquipment(id));
        return "equipment/view";
    }

    // 장비 등록
    @GetMapping("/insert.do")
    public String insertForm(){
        return "equipment/insert";
    }

    @PostMapping("/insert.do")
    public String insertEquipment(@ModelAttribute EquipmentRequest equipmentRequest) {
        equipmentService.insertEquipment(equipmentRequest);
        return "redirect:equipment/list.do";
    }

    // 장비 수정
    @GetMapping("/update.do")
    public String updateForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute(equipmentService.getEquipment(id));
        return "equipment/update";
    }

    @PostMapping("/update.do")
    public String updateEquipment(@ModelAttribute EquipmentUpdate equipmentUpdate) {
         equipmentService.updateEquipment(equipmentUpdate);
         return "redirect:equipment/update.do";
    }

    // 장비 삭제
    @DeleteMapping("/delete.do")
    public String deleteEquipment(Long id) {
        equipmentService.deleteEquipment(id);
        return "redirect:/equipment/list.do";
    }

    // 중복 조회
    @GetMapping("/check-serialNumber")
    public String checkSerialNumber(@RequestParam String number, Model model) {
        model.addAttribute(equipmentService.checkSerialNumber(number));
        return "/equipment/insert.do";
    }

    @GetMapping("/check-accessNumber")
    public String checkAccessNumber(@RequestParam String number, Model model) {
        model.addAttribute(equipmentService.checkAccessNumber(number));
        return "/equipment/insert.do";
    }

    // 관리자 수정
    @GetMapping("/director/update.do")
    public String updateDirectorForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute(equipmentService.getEquipment(id));
        return "equipment/director/update";
    }

    // 관리자 수정
    @PostMapping("/director/update.do")
    public String updateDirector(@RequestParam("id") Long id, @RequestParam("director") String director) {
        equipmentService.updateDirector(id, director);
        return "redirect:/equipment/list.do";
    }

    // 상태 수정
    @PostMapping("/status/update.do")
    public String updateStatusEquipment(@RequestParam("id")Long id, @RequestParam("status") Status status) {
        equipmentService.updateStatus(id, status);
        return "redirect:/equipment/status/list.do";
    }


}
