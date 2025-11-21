package biz.eqp_history.vo;

import biz.basetime.Base;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EqpHistoryVO extends Base {

    Long id;            // 1순위: SQL의 id와 일치

    Long eqpId;         // 2순위: SQL의 eqp_id와 일치

    String directorId;  // 3순위: SQL의 director_id와 일치

}
