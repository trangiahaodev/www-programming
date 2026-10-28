package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.*;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderItem;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.event.OrderPlacedEvent;
import iuh.wwwprogramming.exception.InvalidOrderItemException;
import iuh.wwwprogramming.exception.InvalidOrderStatusException;
import iuh.wwwprogramming.exception.OrderItemNotFoundException;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.repository.OrderItemRepository;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.CartService;
import iuh.wwwprogramming.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    public static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.desc("id")
    );

    // Gộp toàn bộ Dependency của 2 nhánh
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            CartService cartService,
                            ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.eventPublisher = eventPublisher;
    }

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            CartService cartService) {
        this(orderRepository, orderItemRepository, productRepository, userRepository, cartService, null);
    }

    // =========================================================================
    // 1. NHÓM HÀM CHO ADMIN (Quản lý đơn hàng)
    // =========================================================================

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
    public List<TopProductStatDTO> getTopOrderedProducts(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        return orderItemRepository.findTopOrderedProducts(PageRequest.of(0, safeLimit))
                .stream()
                .map(row -> new TopProductStatDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()))
                .limit(safeLimit)
                .toList();
    }

    @Override
    public List<OrderAnalyticsDTO> getOrderAnalytics(LocalDate fromDate, LocalDate toDate) {
        return getOrderAnalytics(fromDate, toDate, 5);
    }

    @Override
    public List<OrderAnalyticsDTO> getOrderAnalytics(LocalDate fromDate, LocalDate toDate, int limit) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Khoảng thời gian thống kê không hợp lệ.");
        }
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.plusDays(1).atStartOfDay();
        long days = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        LocalDateTime previousFrom = from.minusDays(days);
        return orderItemRepository.findOrderAnalytics(from, to, previousFrom, from, safeLimit)
                .stream()
                .map(row -> new OrderAnalyticsDTO(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).longValue(),
                        new BigDecimal(row[5].toString()),
                        ((Number) row[6]).longValue()))
                .toList();
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

            int oldQuantity = orderItem.getQuantity() != null ? orderItem.getQuantity() : 0;
            int delta = newQuantity - oldQuantity;

            // Bù trừ Delta Tồn kho & Cập nhật Sold Count an toàn
            if (delta != 0) {
                Product product = orderItem.getProduct();
                if (product == null && orderItem.getProductId() != null && !orderItem.getProductId().isBlank()) {
                    product = productRepository.findById(orderItem.getProductId()).orElse(null);
                }
                if (product == null && orderItem.getProductCode() != null && !orderItem.getProductCode().isBlank()) {
                    product = productRepository.findByProductCode(orderItem.getProductCode()).orElse(null);
                }

                if (product != null) {
                    int availableStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
                    if (delta > 0) {
                        // Tăng số lượng mua -> Kiểm tra tồn kho khả dụng
                        if (availableStock < delta) {
                            String prodName = (product.getName() != null && !product.getName().isBlank())
                                    ? product.getName()
                                    : (orderItem.getProductName() != null ? orderItem.getProductName() : product.getProductCode());
                            throw new IllegalArgumentException(
                                    "Số lượng tồn kho của sản phẩm '" + prodName + "' không đủ để tăng thêm (tồn kho hiện tại: "
                                            + availableStock + ", cần thêm: " + delta + ")!"
                            );
                        }
                        product.setStockQuantity(availableStock - delta);
                        int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
                        product.setSoldCount(currentSold + delta);
                        productRepository.save(product);
                    } else {
                        // Giảm số lượng mua -> Hoàn trả tồn kho và giảm soldCount
                        int release = Math.abs(delta);
                        product.setStockQuantity(availableStock + release);
                        int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
                        product.setSoldCount(Math.max(0, currentSold - release));
                        productRepository.save(product);
                    }
                }
            }

            orderItem.setQuantity(newQuantity);
            BigDecimal unitPrice = orderItem.getUnitPrice() != null ? orderItem.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(newQuantity));
            orderItem.setSubtotal(subtotal);
        }

        BigDecimal newTotalAmount = items.stream()
                .map(it -> it.getSubtotal() != null ? it.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(newTotalAmount);

        orderRepository.save(order);

        return convertToOrderDetailResponseDTO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailResponseDTO cancelOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn hàng không được để trống.");
        }

        Order order = orderRepository.findByIdWithItems(orderId.trim())
                .orElseThrow(() -> new OrderNotFoundException("Đơn hàng yêu cầu không tồn tại hoặc đã bị xóa khỏi hệ thống: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            log.info("Đơn hàng {} đã ở trạng thái hủy trước đó, bỏ qua hoàn kho lặp lại.", order.getOrderCode());
            return convertToOrderDetailResponseDTO(order);
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException("Không thể hủy đơn hàng đã hoàn tất giao hàng.");
        }

        List<OrderItem> items = order.getItems();
        if (items != null) {
            for (OrderItem item : items) {
                Product product = item.getProduct();
                if (product == null && item.getProductId() != null && !item.getProductId().isBlank()) {
                    product = productRepository.findById(item.getProductId()).orElse(null);
                }
                if (product == null && item.getProductCode() != null && !item.getProductCode().isBlank()) {
                    product = productRepository.findByProductCode(item.getProductCode()).orElse(null);
                }

                if (product != null && item.getQuantity() != null && item.getQuantity() > 0) {
                    int qty = item.getQuantity();
                    int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
                    product.setStockQuantity(currentStock + qty);

                    int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
                    product.setSoldCount(Math.max(0, currentSold - qty));

                    productRepository.save(product);
                }
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return convertToOrderDetailResponseDTO(order);
    }

    // =========================================================================
    // 2. NHÓM HÀM CHO CUSTOMER (Tiến hành Checkout và tra cứu đơn)
    // =========================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO placeOrder(HttpSession session, String userEmail, CheckoutRequestDTO request) {
        CartDTO cart = cartService.getCart(session);
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng của bạn đang trống! Vui lòng chọn sản phẩm trước khi đặt hàng.");
        }

        // Hỗ trợ cả khách có tài khoản và khách vãng lai
        User user = null;
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal computedTotal = BigDecimal.ZERO;

        for (CartItemDTO item : cart.getItems().values()) {
            Product product = productRepository.findByIdAndActiveTrue(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm '" + item.getProductName() + "' không còn khả dụng!"));

            if (item.getQuantity() > product.getStockQuantity()) {
                throw new IllegalArgumentException(
                        "Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho (chỉ còn lại " + product.getStockQuantity() + " sản phẩm)!"
                );
            }

            // Trừ tồn kho sản phẩm & tăng số lượng đã bán (soldCount)
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
            product.setSoldCount(currentSold + item.getQuantity());
            productRepository.save(product);

            BigDecimal lineSubtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            computedTotal = computedTotal.add(lineSubtotal);

            // Bắt buộc lưu productId và liên kết Product vào OrderItem theo quy chuẩn
            OrderItem detail = OrderItem.builder()
                    .productId(product.getId())
                    .product(product)
                    .productName(product.getName())
                    .productCode(product.getProductCode())
                    .unitPrice(product.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(lineSubtotal)
                    .build();

            orderItems.add(detail);
        }

        String orderCode = generateUniqueOrderCode();

        // ĐÃ FIX: Chuyển các thuộc tính về chuẩn Entity của V1
        Order order = Order.builder()
                .orderCode(orderCode)
                .user(user)
                .customerName(request.getRecipientName().trim())
                .customerPhone(request.getRecipientPhone().trim())
                .shippingAddress(request.getShippingAddress().trim())
                .note(request.getNote() != null ? request.getNote().trim() : null)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("UNPAID")
                .totalAmount(computedTotal)
                .status(OrderStatus.PENDING) // Ép kiểu Enum
                .build();

        for (OrderItem detail : orderItems) {
            detail.setOrder(order);
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(session);

        // Gửi email xác nhận đơn hàng bất đồng bộ sau khi giao dịch commit thành công
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new OrderPlacedEvent(
                    savedOrder.getId(),
                    savedOrder.getOrderCode(),
                    savedOrder.getCustomerName(),
                    savedOrder.getCustomerPhone(),
                    userEmail,
                    savedOrder.getTotalAmount()
            ));
        }

        return convertToResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderByCode(String orderCode) {
        Order order = orderRepository.findWithDetailsByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với mã: " + orderCode));
        return convertToResponseDTO(order);
    }

    private String generateUniqueOrderCode() {
        int currentYear = Year.now().getValue() % 100;
        Random random = new Random();
        String candidateCode;
        do {
            int seq = 100000 + random.nextInt(900000);
            candidateCode = String.format("ORD%02d%d", currentYear, seq); // Thống nhất tiền tố ORD
        } while (orderRepository.existsByOrderCode(candidateCode));
        return candidateCode;
    }

    // =========================================================================
    // 3. CÁC HÀM MAPPING DỮ LIỆU
    // =========================================================================

    private OrderDetailResponseDTO convertToOrderDetailResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = mapItems(order.getItems());
        int totalItems = itemDTOs.stream().mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0).sum();

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
        List<OrderItemResponseDTO> itemDTOs = mapItems(order.getItems());
        int totalItems = itemDTOs.stream().mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0).sum();

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .statusDisplay(order.getStatus() != null ? order.getStatus().getDisplayName() : "")
                .totalItems(totalItems)
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .items(itemDTOs)
                .build();
    }

    private List<OrderItemResponseDTO> mapItems(List<OrderItem> items) {
        if (items == null) return Collections.emptyList();

        return items.stream()
                .map(item -> OrderItemResponseDTO.builder()
                        .id(item.getId())
                        .productCode(item.getProductCode())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
    }
}
