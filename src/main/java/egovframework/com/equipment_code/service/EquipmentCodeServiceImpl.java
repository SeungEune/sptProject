package egovframework.com.equipment_code.service;


import egovframework.com.equipment_code.dao.EquipmentCodeDAO;
import egovframework.com.equipment_code.dto.EquipmentCodeRequest;
import egovframework.com.equipment_code.dto.EquipmentCodeResponse;
import egovframework.com.equipment_code.dto.EquipmentCodeUpdate;
import egovframework.com.equipment_code.dto.CodeStatisticResponse;
import egovframework.com.equipment_code.mapstruct.EquipmentCodeMapStruct;
import egovframework.com.equipment_code.vo.EquipmentCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentCodeServiceImpl implements EquipmentCodeService {

    private final EquipmentCodeDAO equipmentCodeDAO;

    @Override
    public List<EquipmentCodeResponse> getCodes() {
        return EquipmentCodeMapStruct.INSTANCE.toDto(equipmentCodeDAO.findAll());
    }

    @Override
    public EquipmentCodeResponse getEquipmentCode(Long codeId) {
        return EquipmentCodeMapStruct.INSTANCE.toDto(equipmentCodeDAO.findById(codeId));
    }

    @Override
    public void insertCode(EquipmentCodeRequest request) {
        EquipmentCodeVO vo = new EquipmentCodeVO().create(request);
        equipmentCodeDAO.save(vo);
    }

    @Override
    public void updateCode(EquipmentCodeUpdate codeUpdate) {
        equipmentCodeDAO.update(new EquipmentCodeVO().update(codeUpdate));
    }

    @Override
    public void deleteCode(Long codeId) {
        equipmentCodeDAO.deleteById(codeId);
    }

    @Override
    public List<EquipmentCodeResponse> getFilteredCode(List<String> name) {
        return EquipmentCodeMapStruct.INSTANCE.toDto(equipmentCodeDAO.findByName(name));
    }

    @Override
    public CodeStatisticResponse getStatistic(Long codeId) {
        return null;
    }
}
