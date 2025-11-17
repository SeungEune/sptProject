package biz.enter.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class EnterVO {
    private Long enterId;
    private String userId;

    private String method;
    private String status;
    private String type;
    private Timestamp registDt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endGuestDt;

    // 직원용(조인 결과)
    private String name;
    private String phone;
    private String email;
    private String jssfcCd;

    // 게스트용
    private String guestNm;
    private String guestPhone;
    private String guestEmail;
}


