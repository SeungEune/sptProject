package biz.mapper;


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

    // 담당자 변경
    void updateDirector(@Param("id") Long id, @Param("director") String director);

    // 상태 변경
    void updateStatus(@Param("id") Long id, @Param("status") Status status);

}
