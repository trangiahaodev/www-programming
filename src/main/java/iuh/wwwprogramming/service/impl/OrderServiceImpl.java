package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderItemQuantityUpdateDTO;
import iuh.wwwprogramming.dto.OrderItemResponseDTO;
import iuh.wwwprogramming.dto.OrderQuantityUpdateRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderItem;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.exception.InvalidOrderItemException;
import iuh.wwwprogramming.exception.InvalidOrderStatusException;
import iuh.wwwprogramming.exception.OrderItemNotFoundException;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.repository.OrderItemRepository;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    public static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.desc("id")
    );

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

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

    @Override
    public OrderDetailResponseDTO getOrderDetailById(String id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new OrderNotFoundException("Đơn hàng yêu cầu không tồn tại hoặc đã bị gỡ bỏ khỏi hệ thống: " + id));

        return convertToOrderDetailResponseDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailResponseDTO updateOrderQuantities(String orderId, OrderQuantityUpdateRequestDTO requestDTO) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn hàng không được để trống.");
        }
        if (requestDTO == null || requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách mặt hàng cập nhật không được để trống.");
        }
        if (requestDTO.getOrderId() != null && !requestDTO.getOrderId().trim().equalsIgnoreCase(orderId.trim())) {
            throw new IllegalArgumentException("Mã đơn hàng trong URL và nội dung yêu cầu không khớp.");
        }

        Order order = orderRepository.findByIdWithItems(orderId.trim())
                .orElseThrow(() -> new OrderNotFoundException("Đơn hàng yêu cầu không tồn tại hoặc đã bị xóa khỏi hệ thống."));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.SHIPPED) {
            String statusName = order.getStatus() != null ? order.getStatus().getDisplayName() : "không xác định";
            throw new InvalidOrderStatusException("Đơn hàng ở trạng thái " + statusName + " không được phép thay đổi số lượng sản phẩm.");
        }

        List<OrderItem> items = order.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Đơn hàng không có sản phẩm nào để cập nhật.");
        }

        Map<String, OrderItem> orderItemMap = items.stream()
                .collect(Collectors.toMap(OrderItem::getId, item -> item));

        Set<String> seenIds = new HashSet<>();
        for (OrderItemQuantityUpdateDTO itemUpdate : requestDTO.getItems()) {
            String itemId = itemUpdate.getOrderItemId();
            if (itemId == null || itemId.trim().isEmpty()) {
                throw new IllegalArgumentException("Mã chi tiết đơn hàng không được để trống.");
            }
            itemId = itemId.trim();

            if (!seenIds.add(itemId)) {
                throw new IllegalArgumentException("Danh sách cập nhật chứa mã sản phẩm trùng lặp: " + itemId);
            }

            Integer newQuantity = itemUpdate.getQuantity();
            if (newQuantity == null || newQuantity < 1 || newQuantity > 999) {
                throw new IllegalArgumentException("Dữ liệu cập nhật không hợp lệ. Số lượng sản phẩm phải là số nguyên từ 1 đến 999.");
            }

            OrderItem orderItem = orderItemMap.get(itemId);
            if (orderItem == null) {
                boolean itemExists = orderItemRepository.existsById(itemId);
                if (itemExists) {
                    log.warn("Security Alert (IDOR detected): Mặt hàng {} không thuộc về đơn hàng {}", itemId, orderId);
                    throw new InvalidOrderItemException("Mặt hàng cập nhật không thuộc về đơn hàng này.");
                } else {
                    throw new OrderItemNotFoundException("Không tìm thấy mặt hàng cần cập nhật trong hệ thống.");
                }
            }

            orderItem.setQuantity(newQuantity);
            BigDecimal subtotal = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity));
            orderItem.setSubtotal(subtotal);
        }

        BigDecimal newTotalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(newTotalAmount);

        orderRepository.save(order);

        return convertToOrderDetailResponseDTO(order);
    }

    private OrderDetailResponseDTO convertToOrderDetailResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = (order.getItems() != null)
                ? order.getItems().stream()
                        .map(item -> OrderItemResponseDTO.builder()
                                .id(item.getId())
                                .productCode(item.getProductCode())
                                .productName(item.getProductName())
                                .unitPrice(item.getUnitPrice())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build())
                        .toList()
                : Collections.emptyList();

        int totalItems = itemDTOs.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();

        return OrderDetailResponseDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .orderDate(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .status(order.getStatus())
                .statusDisplay(order.getStatus() != null ? order.getStatus().getDisplayName() : "")
                .totalItems(totalItems)
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .items(itemDTOs)
                .build();
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
