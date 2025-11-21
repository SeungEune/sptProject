package biz.lunch.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
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
    private String payerName;
    private Integer totalAmount;
    private String type;
    private String participants;
    private Integer totalParticipantAmount;

    // 검색용 날짜 범위
    private String startDate;
    private String endDate;

    private List<ParticipantVO> participantList;
    // 화면의 <input name="participantUserIds"> 등을 받기 위함
    private List<String> participantUserIds;
    private List<Integer> participantAmounts;

    /**
     * 화면에서 받은 배열(List<String>, List<Integer>) 데이터를 DB 처리용 List<ParticipantVO>로 변환하여 세팅
     */
    public void makeParticipantList() {
        // 초기화
        this.participantList = new ArrayList<>();
        // null 체크
        if (participantUserIds == null || participantAmounts == null) {
            return;
        }
        // 3. 두 리스트의 사이즈가 다르면 처리 중단 (IndexOutOfBoundsException 방지)
        if (participantUserIds.size() != participantAmounts.size()) {
            return;
        }
        for (int i = 0; i < participantUserIds.size(); i++) {
            String userId = participantUserIds.get(i);
            Integer amount = participantAmounts.get(i);
            // 4. ID 유효성 체크 (null이거나 빈 문자열이면 스킵)
            if (userId == null || userId.trim().isEmpty()) {
                continue;
            }
            // 5. 금액 null 방어
            if (amount == null) {
                amount = 0;
            }
            ParticipantVO vo = ParticipantVO.builder()
                    .userId(userId)
                    .individualAmount(amount)
                    .build();
            this.participantList.add(vo);
        }
    }
}