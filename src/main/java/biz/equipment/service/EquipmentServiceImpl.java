package biz.equipment.service;

import biz.equipment.dao.EquipmentDAO;
import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentResponse;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.mapstruct.EquipmentMapStruct;
import biz.equipment.vo.EquipmentVO;
import biz.equipment.vo.Status;
import biz.user.service.UserService;
import biz.user.vo.UserVO;
import egovframework.com.cmm.ErrorCode;
import egovframework.com.cmm.exception.custom.NoContentException;
import egovframework.com.cmm.handler.exceptionHandler.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    final EquipmentDAO equipmentDAO;
    private final EquipmentMapStruct equipmentMapStruct;
    private final UserService userService;

    @Override
    public List<EquipmentResponse> getEquipments() {
        return equipmentMapStruct.toDto(equipmentDAO.findAll());
    }

    @Override
    public EquipmentResponse getEquipment(Long id) {
        equipmentDAO.findByIdOrElseThrow(id);
        return equipmentMapStruct.toDto(equipmentDAO.findById(id));
    }

    @Override
    public void insertEquipment(EquipmentRequest request) {
        equipmentDAO.save(new EquipmentVO().create(request));
    }

    @Override
    public void updateEquipment(EquipmentUpdate request) {
        equipmentDAO.findByIdOrElseThrow(request.getId());
        equipmentDAO.update(new EquipmentVO().update(request));
    }

    @Override
    public void deleteEquipment(Long id) {
        equipmentDAO.findByIdOrElseThrow(id);
        equipmentDAO.deleteById(id);
    }

    @Override
    public String checkSerialNumber(String serialNumber) {
        return equipmentDAO.findBySerialNumber(serialNumber);
    }

    @Override
    public String checkAccessNumber(String accessNumber) {
        return equipmentDAO.findByAccessNumber(accessNumber);
    }

    @Override
    public void updateDirector(Long id, String director) {
        equipmentDAO.updateDirector(id, director);
    }

    @Override
    public void updateStatus(Long id, Status status) {
        equipmentDAO.updateStatus(id, status);
    }

    @Override
    public List<String> getDirector(String name) throws Exception {
        return userService.getUserByName(name);
    }
}
