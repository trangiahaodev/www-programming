package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderAnalyticsDTO {
    private String productName;
    private String brand;
    private String image;
    private long orderedQuantity;
    private long orderCount;
    private java.math.BigDecimal revenue;
    private long previousOrderedQuantity;

    public long getTrendPercent() {
        if (previousOrderedQuantity == 0) {
            return orderedQuantity > 0 ? 100 : 0;
        }
        return Math.round((orderedQuantity - previousOrderedQuantity) * 100.0 / previousOrderedQuantity);
    }

    public boolean isTrendUp() {
        return getTrendPercent() >= 0;
    }
}
