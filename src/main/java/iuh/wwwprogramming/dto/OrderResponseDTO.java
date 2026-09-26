package iuh.wwwprogramming.dto;

import iuh.wwwprogramming.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String id;
    private String orderCode;

    // GIỮ V1: Đồng bộ với Entity (customerName thay vì recipientName)
    private String customerName;
    private String customerPhone;

    private String shippingAddress;

    // TỪ V2: Bổ sung thêm ghi chú đơn hàng
    private String note;

    // Gom chung orderDate (V1) và createdAt (V2) thành một biến
    private LocalDateTime createdAt;

    // GIỮ V1: Sử dụng Enum an toàn hơn String
    private OrderStatus status;
    private String statusDisplay;

    private Integer totalItems;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;

    // ĐÃ FIX TỪ V2: Sử dụng danh sách món hàng nhưng theo tên DTO đã chốt (OrderItemResponseDTO)
    @Builder.Default
    private List<OrderItemResponseDTO> items = new ArrayList<>();
}