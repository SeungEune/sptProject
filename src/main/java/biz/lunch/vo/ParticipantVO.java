package biz.lunch.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 점심/커피 참여자 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantVO {
    private Integer lunchId;
    private String userId;
    private String userName;
    private Integer individualAmount;
    private String isPaid;  // 'Y' or 'N'
    private LocalDateTime paidAt;
}
