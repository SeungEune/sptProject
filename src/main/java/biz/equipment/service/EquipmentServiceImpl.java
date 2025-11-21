package biz.equipment.service;

import biz.eqp_history.dao.EqpHistoryDAO;
import biz.eqp_history.vo.EqpHistoryVO;
import biz.equipment.dao.EquipmentDAO;
import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentResponse;
import biz.equipment.dto.EquipmentUpdate;
import biz.equipment.mapstruct.EquipmentMapStruct;
import biz.equipment.vo.DirectorVO;
import biz.equipment.vo.EquipmentVO;
import biz.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    final EquipmentDAO equipmentDAO;
    private final EquipmentMapStruct equipmentMapStruct;
    private final UserService userService;
    private final EqpHistoryDAO eqpHistoryDAO;

    @Override
    public List<EquipmentResponse> getEquipments() {
        return equipmentMapStruct.toDto(equipmentDAO.findAll());
    }

    @Override
    public EquipmentResponse getEquipment(Long id) {
        equipmentDAO.findByIdOrElseThrow(id);
        return equipmentMapStruct.toDto(equipmentDAO.findById(id));
    }

    @Transactional
    @Override
    public void insertEquipment(EquipmentRequest request) {
        EquipmentVO equipmentVO =new EquipmentVO().create(request);

        equipmentDAO.save(equipmentVO);

        eqpHistoryDAO.insertEqpHistory( createEqpHistoryVO(equipmentVO.getId(), equipmentVO.getDirectorId()));
    }

    @Transactional
    @Override
    public void updateEquipment(EquipmentUpdate request) {
        EquipmentVO equipmentVO = equipmentDAO.findByIdOrElseThrow(request.getId());
        equipmentDAO.update(new EquipmentVO().update(request));

        if (!request.getDirectorId().equals(equipmentVO.getDirectorId())) {
            eqpHistoryDAO.insertEqpHistory( createEqpHistoryVO(equipmentVO.getId(), equipmentVO.getDirectorId()));
        }
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
    public List<DirectorVO> getDirector(String name) throws Exception {

        return userService.getUserByName(name).stream()
                .map(userVO -> DirectorVO.builder()
                        .directorId(userVO.getUserId())
                        .director(userVO.getName())
                        .build())
                .collect(Collectors.toList());

    }

    private EqpHistoryVO createEqpHistoryVO(Long eqpId, String directorId) {
        return EqpHistoryVO.builder()
                .eqpId(eqpId)
                .directorId(directorId)
                .build();
    }
}
