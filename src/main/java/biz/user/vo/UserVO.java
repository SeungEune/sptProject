package biz.user.vo;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;
import java.sql.Timestamp;

@Data
public class UserVO {
    @NotBlank @Size(min = 4, max = 20)
    private String userId;

    @Size(min = 8, max = 100)
    private String password;

    // 비밀번호 확인(서버에서만 검증용, DB 칼럼 아님)
    @NotBlank
    private String passwordChk;

    @NotBlank
    private String jssfcCd;

    @NotBlank
    private String name;

    @Pattern(regexp = "^(010)-\\d{4}-\\d{4}$", message = "010-0000-0000 형식")
    private String phone;

    @Email @NotBlank
    private String email;

    // 등록자(관리자 아이디 등)
    private String registerId;

    private Timestamp registDt;
}
