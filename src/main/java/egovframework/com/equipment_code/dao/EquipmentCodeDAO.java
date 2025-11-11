package egovframework.com.equipment_code.dao;

import egovframework.com.cmm.service.impl.EgovComAbstractDAO;
import egovframework.com.equipment_code.mapper.EquipmentCodeMapper;
import egovframework.com.equipment_code.vo.EquipmentCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("equipmentCodeDAO")
@RequiredArgsConstructor
public class EquipmentCodeDAO extends EgovComAbstractDAO {

    private final EquipmentCodeMapper equipmentCodeMapper;

    public List<EquipmentCodeVO> findAll() {
        return equipmentCodeMapper.findAll();
    }
    public EquipmentCodeVO findById(Long id) {return equipmentCodeMapper.findById(id);}
    public int save(EquipmentCodeVO equipmentCodeVO) {return equipmentCodeMapper.save(equipmentCodeVO);}
    public void update(EquipmentCodeVO equipmentCodeVO) {equipmentCodeMapper.update(equipmentCodeVO);}
    public void deleteById(Long id) {equipmentCodeMapper.deleteById(id);}
    public void deleteAll() {equipmentCodeMapper.deleteAll();}
}
