package egovframework.com.equipment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/equipment_code")
@RequiredArgsConstructor
public class EquipmentController {

    // 장비 조회
    public Map<String, Object> getEquipment() {
        return Map.of();
    }

    // 장비 등록
    public Map<String, Object> insertEquipment(Map<String, Object> equipment) {
        return Map.of();
    }

    // 장비 수정
    public Map<String, Object> updateEquipment(Map<String, Object> equipment) {
        return Map.of();
    }

    // 장비 삭제
    public Map<String, Object> deleteEquipment(Long equipmentId) {
        return Map.of();
    }

    // 장비 중복 검증 - 자산번호
    public Optional<Map<String, Object>> validateNumber(String assetNumber) {
        return Optional.ofNullable(Map.of());
    }

    // 장비 상태 관리
    public Map<String, Object> updateEquipmentStatus(Map<String, Object> equipment) {
        return Map.of();
    }

    // 장비 변경 이력 관리
    public Map<String, Object> getDirectorHistory(Long equipmentId) {
        return Map.of();
    }

    // 장비 담당자 관리
    public Map<String, Object> updateDirectorHistory(Map<String, Object> equipment) {
        return Map.of();
    }

}
