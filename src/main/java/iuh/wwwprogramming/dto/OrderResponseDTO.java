package iuh.wwwprogramming.dto;

import iuh.wwwprogramming.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String id;
    private String orderCode;
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String statusDisplay;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
}
