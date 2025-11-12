package biz.equipment_code.vo;


import biz.basetime.Base;
import biz.equipment_code.dto.EquipmentCodeRequest;
import biz.equipment_code.dto.EquipmentCodeUpdate;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EquipmentCodeVO extends Base {

    private Long id;

    private String code;

    private String name;

    public EquipmentCodeVO create(EquipmentCodeRequest equipmentCodeRequest) {
        return EquipmentCodeVO.builder()
                .code(equipmentCodeRequest.getCode())
                .name(equipmentCodeRequest.getName())
                .build();
    }

    public EquipmentCodeVO update(EquipmentCodeUpdate equipmentCodeUpdate) {
        return EquipmentCodeVO.builder()
                .id(equipmentCodeUpdate.getId())
                .code(equipmentCodeUpdate.getCode())
                .name(equipmentCodeUpdate.getName())
                .build();
    }
}
