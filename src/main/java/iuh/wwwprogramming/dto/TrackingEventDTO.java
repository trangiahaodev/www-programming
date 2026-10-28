package iuh.wwwprogramming.dto;

import lombok.Data;

@Data
public class TrackingEventDTO {
    private String eventType;
    private String action;
    private String productId;
    private String productName;
    private Integer clickCount;
    private Integer dwellTimeSeconds;
    private Integer durationSeconds;
    private Integer deltaSeconds;
    private Integer addToCartCount;
}
