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
    String serialNumber;
    String directorId;
    String directorName;
    LocalDateTime createdAt;
}
