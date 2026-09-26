package iuh.wwwprogramming.dto;

import iuh.wwwprogramming.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDTO {
    private String id;
    private String orderCode;
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private String note;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private OrderStatus status;
    private String statusDisplay;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private List<OrderItemResponseDTO> items;
}
