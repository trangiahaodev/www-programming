package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderItem;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.desc("id")
    );

    private final OrderRepository orderRepository;

    @Override
    public Page<OrderResponseDTO> getOrders(OrderFilterDTO filterDTO, Pageable pageable) {
        if (filterDTO == null) {
            filterDTO = new OrderFilterDTO();
        }

        if (filterDTO.isDateRangeInvalid()) {
            throw new IllegalArgumentException("Khoảng thời gian không hợp lệ: Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
        }

        String keyword = (filterDTO.getKeyword() != null && !filterDTO.getKeyword().trim().isEmpty())
                ? filterDTO.getKeyword().trim()
                : null;

        LocalDateTime startDateTime = filterDTO.getFromDate() != null
                ? filterDTO.getFromDate().atStartOfDay()
                : null;

        LocalDateTime endDateTime = filterDTO.getToDate() != null
                ? filterDTO.getToDate().atTime(LocalTime.MAX)
                : null;

        Pageable sortedPageable = (pageable != null && pageable.getSort().isSorted())
                ? pageable
                : PageRequest.of(
                        pageable != null ? pageable.getPageNumber() : 0,
                        pageable != null ? pageable.getPageSize() : 10,
                        DEFAULT_SORT
                );

        Page<Order> orderPage = orderRepository.searchOrders(
                keyword,
                filterDTO.getStatus(),
                startDateTime,
                endDateTime,
                sortedPageable
        );

        return orderPage.map(this::convertToResponseDTO);
    }

    @Override
    public OrderResponseDTO getOrderById(String id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new OrderNotFoundException("Đơn hàng yêu cầu không tồn tại hoặc đã bị gỡ bỏ khỏi hệ thống: " + id));
        return convertToResponseDTO(order);
    }

    private OrderResponseDTO convertToResponseDTO(Order order) {
        int totalItems = 0;
        if (order.getItems() != null) {
            totalItems = order.getItems().stream()
                    .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                    .sum();
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .orderDate(order.getCreatedAt())
                .status(order.getStatus())
                .statusDisplay(order.getStatus() != null ? order.getStatus().getDisplayName() : "")
                .totalItems(totalItems)
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .build();
    }
}
