package biz.equipment_code.service;


import biz.equipment_code.dao.EquipmentCodeDAO;
import biz.equipment_code.dto.EquipmentCodeRequest;
import biz.equipment_code.dto.EquipmentCodeResponse;
import biz.equipment_code.dto.EquipmentCodeUpdate;
import biz.equipment_code.dto.CodeStatisticResponse;
import biz.equipment_code.mapstruct.EquipmentCodeMapStruct;
import biz.equipment_code.vo.EquipmentCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentCodeServiceImpl implements EquipmentCodeService {

    private final EquipmentCodeDAO equipmentCodeDAO;
    private final EquipmentCodeMapStruct equipmentCodeMapStruct;

    @Override
    public List<EquipmentCodeResponse> getCodes() {
        return equipmentCodeMapStruct.toDto(equipmentCodeDAO.findAll());
    }

    @Override
    public EquipmentCodeResponse getEquipmentCode(Long codeId) {
        return equipmentCodeMapStruct.toDto(equipmentCodeDAO.findById(codeId));
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
        return equipmentCodeMapStruct.toDto(equipmentCodeDAO.findByName(name));
    }

    @Override
    public CodeStatisticResponse getStatistic(Long codeId) {
        return null;
    }
}
