package biz.lunch.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 점심/커피 마스터 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LunchVO {
    private Integer lunchId;
    private String date;
    private String storeName;
    private String payerId;
    private String payerName;  // 조회용 (JOIN 결과)
    private Integer totalAmount;
    private String type;
    private String participants;  // 조회용 (STRING_AGG 결과: "user1:10000, user2:15000")
    private Integer totalParticipantAmount;  // 조회용 (SUM 결과)
    
    // 등록/수정용
    private List<ParticipantVO> participantList;
}
