package biz.equipment.dto;

import biz.equipment.vo.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentUpdate {

    private Long id;

    private String serialNumber;

    private String  accessNumber;

    private String director;

    private Status status;
}
