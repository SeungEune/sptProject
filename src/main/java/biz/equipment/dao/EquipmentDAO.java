package biz.equipment.dao;

import egovframework.com.cmm.service.impl.EgovComAbstractDAO;
import biz.equipment.mapper.EquipmentMapper;
import biz.equipment.vo.EquipmentVO;
import biz.equipment.vo.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public void updateDirector(Long id, String director) {
        equipmentMapper.updateDirector(id, director);
    }

    public void updateStatus(Long id, Status status) {
        equipmentMapper.updateStatus(id, status);
    }
}
