package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventDTO {
    private String eventType;
    private String action;
    private String productId;
    private String productName;
    private Integer clickCount;
    private Integer dwellTime;
    private Integer dwellTimeSeconds;
    private Integer durationSeconds;
    private Integer deltaSeconds;
    private Integer addToCartCount;
    private String pageUrl;
    private String timestamp;
}
