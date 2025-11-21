package biz.mapper.biz.equipment;


import biz.equipment.vo.EquipmentVO;
import biz.equipment.vo.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EquipmentMapper {

    // 전체 조회
    List<EquipmentVO> findAll();

    // 단건 조회
    EquipmentVO findById(@Param("id") Long id);

    // 삽입
    int save(@Param("equipment") EquipmentVO equipmentVO);

    // 수정
    void update(@Param("equipment") EquipmentVO equipmentVO);

    // 삭제
    void deleteById(@Param("id") Long id);

    // 전체 삭제
    void deleteAll();

    String duplicateChkSerialNumber(@Param("serialNumber") String serialNumber);

    String duplicateChkAccessNumber(@Param("accessNumber") String accessNumber);

    EquipmentVO selectBySerialNumber(@Param("serialNumber") String serialNumber);

}
