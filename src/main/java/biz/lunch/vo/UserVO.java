package biz.lunch.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 사용자 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {
    private String userId;
    private String userName;
}
