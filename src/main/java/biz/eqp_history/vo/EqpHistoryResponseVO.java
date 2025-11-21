package biz.eqp_history.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
public class EqpHistoryResponseVO {

    Long id;
    Long eqpId;
    String directorId;
    String serialNumber;
    String directorName;
    LocalDateTime createdAt;
}
