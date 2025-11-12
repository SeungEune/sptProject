package biz.equipment.vo;

import lombok.Getter;

@Getter
public enum Status {

    USE("사용중"), STORAGE("보관중"), REPAIR("수리중"), DISPOSAL("폐기");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}
