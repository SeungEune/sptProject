package biz.user.vo;

import lombok.Data;

@Data
public class UserSearchCond {
    private String option;
    private String keyword;
    private String jssfcCd;
    //private String registMonth; // YYYY-MM
    private String startDate;  // yyyy-MM-dd
    private String endDate;    // yyyy-MM-dd

    // 페이징
    private int page;   // 현재 페이지 (1부터)
    private int size;   // 페이지당 개수
    private int offset; // LIMIT/OFFSET 용
}
