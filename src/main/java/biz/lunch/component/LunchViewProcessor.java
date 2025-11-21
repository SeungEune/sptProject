package biz.lunch.component;

import biz.lunch.vo.LunchVO;
import egovframework.com.cmm.service.EgovProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Lunch 데이터를 화면(HTML Table)에 맞게 가공
 */
@Component
public class LunchViewProcessor {

    public List<Map<String, Object>> convertToFlatList(List<LunchVO> lunchList) {
        String representativeName = EgovProperties.getProperty("lunch.representative.name");
        List<Map<String, Object>> flatLunchList = new ArrayList<>();

        // 1. 날짜별 그룹화
        Map<String, List<LunchVO>> byDate = new LinkedHashMap<>();
        for (LunchVO item : lunchList) {
            if (item.getDate() == null) continue;
            byDate.computeIfAbsent(item.getDate(), k -> new ArrayList<>()).add(item);
        }

        for (Map.Entry<String, List<LunchVO>> entry : byDate.entrySet()) {
            List<LunchVO> itemsForDate = entry.getValue();
            List<Map<String, Object>> dailyRows = new ArrayList<>();

            for (LunchVO item : itemsForDate) {
                boolean isRepresentative = representativeName.equals(item.getPayerName());

                Map<String, Object> detailRow = toMap(item);
                detailRow.put("rowType", "DETAIL");
                dailyRows.add(detailRow);

                if (!isRepresentative) {
                    Map<String, Object> payRow = toMap(item);
                    payRow.put("rowType", "PAY");
                    dailyRows.add(payRow);
                }
            }

            // 3. RowSpan 계산 및 첫 행 마킹
            int rowSpan = dailyRows.size();
            for (int i = 0; i < dailyRows.size(); i++) {
                Map<String, Object> row = dailyRows.get(i);
                row.put("isFirstOfDate", i == 0); // 날짜는 첫 줄에만 표시
                if (i == 0) {
                    row.put("dateRowSpan", rowSpan); // 날짜 셀 병합 개수
                }
                flatLunchList.add(row);
            }
        }

        return flatLunchList;
    }

    /**
     * VO -> Map 변환 유틸 메서드
     */
    private Map<String, Object> toMap(LunchVO item) {
        Map<String, Object> map = new HashMap<>();
        map.put("lunchId", item.getLunchId());
        map.put("date", item.getDate());
        map.put("storeName", item.getStoreName());
        map.put("totalAmount", item.getTotalAmount());
        map.put("type", item.getType());
        map.put("payerId", item.getPayerId());
        map.put("payerName", item.getPayerName());
        map.put("participants", item.getParticipants());
        return map;
    }
}