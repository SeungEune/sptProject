package biz.equipment.dto;

import biz.equipment.vo.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class EquipmentRequest {

    private Long id;

    private String serialNumber;

    private String  accessNumber;

    private String  directorId;

    private String director;

    private Status status;

}
