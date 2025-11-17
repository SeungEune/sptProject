package biz.equipment.dto;


import biz.equipment.vo.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentResponse {

    private Long id;

//    private String code;
//
//    private String name;

    private String serialNumber;

    private String  accessNumber;

    private String director;

    private Status status;

}
