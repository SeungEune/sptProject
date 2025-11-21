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


    public static UserVOBuilder builder() {
        return new UserVOBuilder();
    }

    public static class UserVOBuilder {
        private final UserVO user = new UserVO();

        public UserVOBuilder userId(String userId) {
            user.setUserId(userId);
            return this;
        }

        public UserVOBuilder password(String password) {
            user.setPassword(password);
            return this;
        }

        public UserVOBuilder passwordChk(String passwordChk) {
            user.setPasswordChk(passwordChk);
            return this;
        }

        public UserVOBuilder jssfcCd(String jssfcCd) {
            user.setJssfcCd(jssfcCd);
            return this;
        }

        public UserVOBuilder name(String name) {
            user.setName(name);
            return this;
        }

        public UserVOBuilder phone(String phone) {
            user.setPhone(phone);
            return this;
        }

        public UserVOBuilder email(String email) {
            user.setEmail(email);
            return this;
        }

        public UserVOBuilder registerId(String registerId) {
            user.setRegisterId(registerId);
            return this;
        }

        public UserVOBuilder registDt(Timestamp registDt) {
            user.setRegistDt(registDt);
            return this;
        }

        public UserVO build() {
            return user;
        }
    }
}
