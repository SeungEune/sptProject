package biz.lunch.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 점심/커피 통계 요약 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryVO {
    private String userId;
    private String userName;
    private String month;  // "YYYY-MM" 형식
    private Integer totalPaid;  // 총 결제금액
    private Integer totalOwed;  // 총 부담금액
    private Integer balance;  // 잔액 (totalPaid - totalOwed)
    private String representativePayerId;  // 대표 정산자 ID
    private String representativePayerName;  // 대표 정산자 이름
    private String isSettled;  // 정산 완료 여부 ('Y' or 'N')
}
