package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopProductStatDTO {
    private String productName;
    private long orderedQuantity;
    private long orderCount;
}
