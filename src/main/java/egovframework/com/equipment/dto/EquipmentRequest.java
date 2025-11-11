package egovframework.com.equipment.dto;

import egovframework.com.equipment.vo.Status;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
@Builder
public class EquipmentRequest {

    Long id;

    private String code;

    private String name;

    private String serialNumber;

    private String  accessNumber;

    private String director;

    private Status status;

}
