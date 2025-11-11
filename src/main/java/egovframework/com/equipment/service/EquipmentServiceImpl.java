package egovframework.com.equipment.service;

import egovframework.com.equipment.dao.EquipmentDAO;
import egovframework.com.equipment.dto.EquipmentRequest;
import egovframework.com.equipment.dto.EquipmentResponse;
import egovframework.com.equipment.dto.EquipmentUpdate;
import egovframework.com.equipment.mapstruct.EquipmentMapStruct;
import egovframework.com.equipment.vo.EquipmentVO;
import egovframework.com.equipment.vo.Status;
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
