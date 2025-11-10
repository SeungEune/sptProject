package egovframework.com.equipment.vo;


import egovframework.com.basetime.Base;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EquipmentCodeVO extends Base {

    private Long id;

    private String code;

    private String type;
}
