package egovframework.com.equipment.vo;


import egovframework.com.basetime.Base;
import egovframework.com.equipment.dto.EquipmentRequest;
import egovframework.com.equipment.dto.EquipmentUpdate;
import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EquipmentVO extends Base {

    private Long id;

    private String code;

    private String name;

    private String serialNumber;

    private String  accessNumber;

    private String director;

    private Status status = Status.STORAGE;


    public EquipmentVO create(EquipmentRequest equipmentRequest) {
        return EquipmentVO.builder()
                .code(equipmentRequest.getCode())
                .name(equipmentRequest.getName())
                .serialNumber(equipmentRequest.getSerialNumber())
                .accessNumber(equipmentRequest.getAccessNumber())
                .director(equipmentRequest.getDirector())
                .status(equipmentRequest.getStatus())
                .build();
    }

    public EquipmentVO update(EquipmentUpdate equipmentUpdate) {
        return EquipmentVO.builder()
                .id(equipmentUpdate.getId())
                .serialNumber(equipmentUpdate.getSerialNumber())
                .accessNumber(equipmentUpdate.getAccessNumber())
                .director(equipmentUpdate.getDirector())
                .status(equipmentUpdate.getStatus())
                .build();
    }
}
