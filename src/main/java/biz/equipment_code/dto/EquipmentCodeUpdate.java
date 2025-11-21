package biz.equipment_code.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentCodeUpdate {
    Long id;
    String code;
    String name;
}
