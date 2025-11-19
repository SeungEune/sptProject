package biz.equipment_history.vo;

import biz.basetime.Base;
import lombok.Builder;

@Builder
public class eqipHistoryVO extends Base {

    Long id;

    Long userId;

    Long equipmentId;
}
