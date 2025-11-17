package biz.equipment.service;



import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentResponse;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.vo.Status;

import java.util.List;


public interface EquipmentService {
    // 장비 조회
    List<EquipmentResponse> getEquipments();

    // 장비단건 조회
    EquipmentResponse getEquipment(Long id);

    // 장비 등록
    void insertEquipment(EquipmentRequest request);

    // 장비 수정
    void updateEquipment(EquipmentUpdate request);

    // 장비 분류코드 삭제
    void deleteEquipment(Long id);

    // 코드 중복 검증
    String checkSerialNumber(String serialNumber);

    String checkAccessNumber(String accessNumber);

    // 담당자 변경
    void updateDirector(Long id, String director);

    // 상태 변경
    void updateStatus(Long id, Status status);

}
