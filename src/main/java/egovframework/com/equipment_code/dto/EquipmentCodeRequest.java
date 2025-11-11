package egovframework.com.equipment_code.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentCodeRequest {

    Long id;
    String code;
    String name;

}
