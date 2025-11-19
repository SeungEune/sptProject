package biz.equipment_history.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class EquipHistoryResponse {

    String directorName;

    String serialNumber;

    LocalDateTime createdAt;
}
