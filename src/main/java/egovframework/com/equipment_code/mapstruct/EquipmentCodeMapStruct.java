package egovframework.com.equipment_code.mapstruct;

import egovframework.com.equipment_code.dto.EquipmentCodeResponse;
import egovframework.com.equipment_code.vo.EquipmentCodeVO;
import lombok.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipmentCodeMapStruct {

    EquipmentCodeMapStruct INSTANCE = Mappers.getMapper(EquipmentCodeMapStruct.class);

    EquipmentCodeResponse toDto(EquipmentCodeVO vo);

    @Mapping(target = "code" ,source = "code")
    @Mapping(target = "name" ,source = "name")
    List<EquipmentCodeResponse> toDto (List<EquipmentCodeVO> equipmentCodeVO);

}
