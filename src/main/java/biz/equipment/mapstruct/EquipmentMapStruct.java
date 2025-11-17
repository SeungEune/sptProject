package biz.equipment.mapstruct;

import biz.equipment.dto.EquipmentResponse;
import biz.equipment.vo.EquipmentVO;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipmentMapStruct {

    EquipmentResponse toDto(EquipmentVO e);

    @Mapping(target = "serialNumber" ,source = "serialNumber")
    @Mapping(target = "accessNumber" ,source = "accessNumber")
    @Mapping(target = "director" ,source = "director")
    @Mapping(target = "status" ,source = "status")
    List<EquipmentResponse> toDto (List<EquipmentVO> equipmentCodeVO);

}
