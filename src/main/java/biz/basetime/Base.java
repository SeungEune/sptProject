package biz.basetime;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Base {
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
