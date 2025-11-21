package biz.mapper.biz.equipment_code;


import biz.equipment_code.vo.EquipmentCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EquipmentCodeMapper {

    // 전체 조회
    List<EquipmentCodeVO> findAll();

    // 단건 조회
    EquipmentCodeVO findById(@Param("id") Long id);

    // 삽입
    int save(@Param("equipmentCode") EquipmentCodeVO equipmentCodeVO);

    // 수정
    void update(@Param("equipmentCode") EquipmentCodeVO equipmentCodeVO);

    // 삭제
    void deleteById(@Param("id") Long id);

    // 전체 삭제
    void deleteAll();

    // 필터 조회
    List<EquipmentCodeVO> findByName(@Param("name") List<String> name);
}
