package biz.equipment.service;

import biz.equipment.dao.EquipmentDAO;
import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentResponse;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.mapstruct.EquipmentMapStruct;
import biz.equipment.vo.EquipmentVO;
import biz.equipment.vo.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    final EquipmentDAO equipmentDAO;

    @Override
    public List<EquipmentResponse> getEquipments() {
        return EquipmentMapStruct.INSTANCE.toDto(equipmentDAO.findAll());
    }

    @Override
    public EquipmentResponse getEquipment(Long id) {
        return EquipmentMapStruct.INSTANCE.toDto(equipmentDAO.findById(id));
    }

    @Override
    public void insertEquipment(EquipmentRequest request) {
        equipmentDAO.save(new EquipmentVO().create(request));
    }

    @Override
    public void updateEquipment(EquipmentUpdate request) {
        equipmentDAO.update(new EquipmentVO(). update(request));
    }

    @Override
    public void deleteEquipment(Long id) {
        equipmentDAO.deleteById(id);
    }

    @Override
    public void updateDirector(Long id, String director) {
        equipmentDAO.updateDirector(id, director);
    }

    @Override
    public void updateStatus(Long id, Status status) {
         equipmentDAO.updateStatus(id, status);
    }
}
