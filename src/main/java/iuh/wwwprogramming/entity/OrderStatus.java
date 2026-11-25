package iuh.wwwprogramming.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELLED("Đã hủy");

    private final String displayName;

    public List<OrderStatus> getAllowedTransitions() {
        return switch (this) {
            case PENDING -> List.of(PROCESSING, CANCELLED);
            case PROCESSING -> List.of(SHIPPED, CANCELLED);
            case SHIPPED -> List.of(DELIVERED, CANCELLED);
            case DELIVERED, CANCELLED -> List.of();
        };
    }
}
