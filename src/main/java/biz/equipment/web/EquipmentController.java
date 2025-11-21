package biz.equipment.web;

import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.service.EquipmentService;
import biz.equipment.vo.DirectorVO;
import biz.equipment.vo.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return "equipment/list";
    }

    // 장비 수정
    @GetMapping("/update.do")
    public String updateForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("equipment",equipmentService.getEquipment(id));
        return "equipment/update";
    }

    @PostMapping("/update.do")
    public String updateEquipment(@ModelAttribute EquipmentUpdate equipmentUpdate) {
         equipmentService.updateEquipment(equipmentUpdate);
         return "redirect:equipment/list.do";
    }

    // 장비 삭제
    @GetMapping("/delete.do")
    public String deleteEquipment(Long id) {
        equipmentService.deleteEquipment(id);
        return "redirect:/equipment/list.do";
    }

    // 중복 조회
    @GetMapping("/check-serialNumber")
    @ResponseBody
    public String checkSerialNumber(@RequestParam String serialNumber) {
        return equipmentService.checkSerialNumber(serialNumber);
    }

    @GetMapping("/check-accessNumber")
    @ResponseBody
    public String checkAccessNumber(@RequestParam String accessNumber) {
        return equipmentService.checkAccessNumber(accessNumber);
    }

     //관리자 찾기
    @GetMapping("/director/view")
    @ResponseBody
    public List<DirectorVO> getDirector(@RequestParam("name") String name) throws Exception {
        return equipmentService.getDirector(name);
    }
}
