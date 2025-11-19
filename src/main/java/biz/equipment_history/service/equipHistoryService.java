package biz.equipment_history.service;

import biz.equipment_history.dto.EquipHistoryResponse;

import java.util.List;

public interface equipHistoryService {
    List<EquipHistoryResponse> getEquipHistories(Long equipId);
}
