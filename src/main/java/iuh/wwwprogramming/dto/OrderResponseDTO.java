package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private String id;
    private String orderCode;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String note;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<OrderDetailResponseDTO> items = new ArrayList<>();
}
