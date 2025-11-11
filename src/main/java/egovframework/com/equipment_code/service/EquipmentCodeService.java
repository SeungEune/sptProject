package egovframework.com.equipment_code.service;

import egovframework.com.equipment_code.dto.EquipmentCodeRequest;
import egovframework.com.equipment_code.dto.EquipmentCodeResponse;
import egovframework.com.equipment_code.dto.EquipmentCodeUpdate;
import egovframework.com.equipment_code.dto.CodeStatisticResponse;

import java.util.List;


public interface EquipmentCodeService {
    // 장비 분류코드 조회
    List<EquipmentCodeResponse> getCodes();

    // 장비 분류코드 단건 조회
    EquipmentCodeResponse getEquipmentCode(Long codeId);

    // 장비 분류코드 등록
    void insertCode(EquipmentCodeRequest request);

    // 장비 분류코드 수정
    void updateCode(EquipmentCodeUpdate request);

    // 장비 분류코드 삭제
    void deleteCode(Long codeId);

    // 장비 분류 필터
    List<EquipmentCodeResponse> getFilteredCode(List<String> code);

    // 장비 분류 통계
    CodeStatisticResponse getStatistic(Long codeId);
}
