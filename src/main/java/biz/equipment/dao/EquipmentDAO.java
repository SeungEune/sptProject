package biz.equipment.dao;

import egovframework.com.cmm.exception.custom.NoContentException;
import egovframework.com.cmm.service.impl.EgovComAbstractDAO;
import biz.mapper.biz.equipment.EquipmentMapper;
import biz.equipment.vo.EquipmentVO;
import biz.equipment.vo.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("equipmentDAO")
@RequiredArgsConstructor
public class EquipmentDAO extends EgovComAbstractDAO {

    private final EquipmentMapper equipmentMapper;

    public List<EquipmentVO> findAll() {
        return equipmentMapper.findAll();
    }

    public EquipmentVO findById(Long id) {
        return equipmentMapper.findById(id);
    }

    public int save(EquipmentVO equipmentVO) {
        return equipmentMapper.save(equipmentVO);
    }

    public void update(EquipmentVO equipmentVO) {
        equipmentMapper.update(equipmentVO);
    }

    public void deleteById(Long id) {
        equipmentMapper.deleteById(id);
    }

    public void deleteAll() {
        equipmentMapper.deleteAll();
    }

    public String findBySerialNumber(String serialNumber) {
        return equipmentMapper.duplicateChkSerialNumber(serialNumber);
    }

    public String findByAccessNumber(String accessNumber) {
        return equipmentMapper.duplicateChkSerialNumber(accessNumber);
    }

    public void updateDirector(Long id, String director) {
        equipmentMapper.updateDirector(id, director);
    }

    public void updateStatus(Long id, Status status) {
        equipmentMapper.updateStatus(id, status);
    }


    public void findByIdOrElseThrow(Long id) {
        Optional.ofNullable(findById(id)).orElseThrow(NoContentException::new);
    }

    public EquipmentVO selectBySerialNumber(String serialNumber) {
        return equipmentMapper.selectBySerialNumber(serialNumber);
    }
}
