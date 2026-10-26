package iuh.wwwprogramming.event;

import java.math.BigDecimal;

public record OrderPlacedEvent(
        String orderId,
        String orderCode,
        String customerName,
        String customerPhone,
        String customerEmail,
        BigDecimal totalAmount
) {}
