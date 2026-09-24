package iuh.wwwprogramming.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELLED("Đã hủy");

    private final String displayName;
}
