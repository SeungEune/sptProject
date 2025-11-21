package biz.equipment.vo;


import biz.basetime.Base;
import biz.equipment.dto.EquipmentRequest;
import biz.equipment.dto.EquipmentUpdate;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EquipmentVO extends Base {

    private Long id;

    private String serialNumber;

    private String  accessNumber;

    private String directorId;

    private String director;

    private Status status = Status.STORAGE;


    public EquipmentVO create(EquipmentRequest equipmentRequest) {
        return EquipmentVO.builder()
                .serialNumber(equipmentRequest.getSerialNumber())
                .accessNumber(equipmentRequest.getAccessNumber())
                .directorId(equipmentRequest.getDirectorId())
                .director(equipmentRequest.getDirector())
                .status(equipmentRequest.getStatus())
                .build();
    }

    public EquipmentVO update(EquipmentUpdate equipmentUpdate) {
        return EquipmentVO.builder()
                .id(equipmentUpdate.getId())
                .serialNumber(equipmentUpdate.getSerialNumber())
                .accessNumber(equipmentUpdate.getAccessNumber())
                .directorId(equipmentUpdate.getDirectorId())
                .director(equipmentUpdate.getDirector())
                .status(equipmentUpdate.getStatus())
                .build();
    }
}
