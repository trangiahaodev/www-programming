package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.TopProductStatDTO;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderItem;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.exception.InvalidOrderStatusException;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.repository.OrderItemRepository;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                orderRepository,
                orderItemRepository,
                productRepository,
                userRepository,
                cartService
        );
    }

    @Test
    void shouldMapFilterValuesAndPassDateBoundariesToRepository() {
        OrderFilterDTO filter = OrderFilterDTO.builder()
                .keyword(" ORD2601 ")
                .status(OrderStatus.PENDING)
                .fromDate(LocalDate.of(2026, 9, 20))
                .toDate(LocalDate.of(2026, 9, 24))
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        Order order = Order.builder()
                .id("1")
                .orderCode("ORD2601")
                .customerName("Khách")
                .customerPhone("0909")
                .shippingAddress("Địa chỉ")
                .totalAmount(BigDecimal.ONE)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.searchOrders(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(order)));

        orderService.getOrders(filter, pageable);

        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderStatus> statusCaptor = ArgumentCaptor.forClass(OrderStatus.class);
        ArgumentCaptor<LocalDateTime> fromCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> toCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).searchOrders(
                keywordCaptor.capture(),
                statusCaptor.capture(),
                fromCaptor.capture(),
                toCaptor.capture(),
                pageableCaptor.capture()
        );

        assertThat(keywordCaptor.getValue()).isEqualTo("ORD2601");
        assertThat(statusCaptor.getValue()).isEqualTo(OrderStatus.PENDING);
        assertThat(fromCaptor.getValue()).isEqualTo(LocalDate.of(2026, 9, 20).atStartOfDay());
        assertThat(toCaptor.getValue()).isEqualTo(LocalDate.of(2026, 9, 24).atTime(LocalTime.MAX));
        assertThat(pageableCaptor.getValue()).isEqualTo(pageable);
    }

    @Test
    void shouldUseDeterministicDefaultSortingWhenPageableIsNull() {
        OrderFilterDTO filter = new OrderFilterDTO();
        when(orderRepository.searchOrders(any(), any(), any(), any(), any()))
                .thenReturn(Page.empty());

        orderService.getOrders(filter, null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).searchOrders(any(), any(), any(), any(), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("createdAt").isDescending()).isTrue();
        assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("id").isDescending()).isTrue();
    }

    @Test
    void shouldMapTopOrderedProductsAndCapRequestedLimit() {
        when(orderItemRepository.findTopOrderedProducts(any(Pageable.class)))
                .thenReturn(List.of(
                        new Object[]{"Kem chống nắng", 12L, 5L},
                        new Object[]{"Sữa rửa mặt", 8L, 4L}
                ));

        List<TopProductStatDTO> result = orderService.getTopOrderedProducts(100);

        assertThat(result).extracting(TopProductStatDTO::getProductName)
                .containsExactly("Kem chống nắng", "Sữa rửa mặt");
        assertThat(result.get(0).getOrderedQuantity()).isEqualTo(12);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderItemRepository).findTopOrderedProducts(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    @Test
    void shouldRejectInvalidDateRange() {
        OrderFilterDTO filter = OrderFilterDTO.builder()
                .fromDate(LocalDate.of(2026, 9, 25))
                .toDate(LocalDate.of(2026, 9, 24))
                .build();

        assertThatThrownBy(() -> orderService.getOrders(filter, PageRequest.of(0, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Khoảng thời gian không hợp lệ: Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
    }

    @Test
    void shouldReturnOrderDetailWithItemsWhenOrderExists() {
        String orderId = "order-uuid-1";
        Order order = Order.builder()
                .id(orderId)
                .orderCode("ORD26000001")
                .customerName("Nguyễn Thị Lan")
                .customerPhone("0987654321")
                .shippingAddress("12 Nguyễn Văn Bảo")
                .note("Giao giờ hành chính")
                .totalAmount(new BigDecimal("650000.00"))
                .paymentMethod("COD")
                .paymentStatus("UNPAID")
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderItem item1 = OrderItem.builder()
                .id("item-1")
                .order(order)
                .productCode("SP001")
                .productName("Sản phẩm A")
                .unitPrice(new BigDecimal("200000.00"))
                .quantity(2)
                .subtotal(new BigDecimal("400000.00"))
                .build();

        OrderItem item2 = OrderItem.builder()
                .id("item-2")
                .order(order)
                .productCode("SP002")
                .productName("Sản phẩm B")
                .unitPrice(new BigDecimal("250000.00"))
                .quantity(1)
                .subtotal(new BigDecimal("250000.00"))
                .build();

        order.setItems(new ArrayList<>(List.of(item1, item2)));

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderDetailResponseDTO result = orderService.getOrderDetailById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        assertThat(result.getOrderCode()).isEqualTo("ORD26000001");
        assertThat(result.getCustomerName()).isEqualTo("Nguyễn Thị Lan");
        assertThat(result.getTotalItems()).isEqualTo(3);
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getProductName()).isEqualTo("Sản phẩm A");
        assertThat(result.getItems().get(1).getProductName()).isEqualTo("Sản phẩm B");
    }

    @Test
    void shouldReturnOrderDetailWithEmptyItemsWhenOrderHasNoItems() {
        String orderId = "order-empty-items";
        Order order = Order.builder()
                .id(orderId)
                .orderCode("ORD26000099")
                .customerName("Trần Văn Không Hàng")
                .customerPhone("0911000000")
                .shippingAddress("Chưa cập nhật")
                .totalAmount(BigDecimal.ZERO)
                .paymentMethod("COD")
                .paymentStatus("UNPAID")
                .status(OrderStatus.PENDING)
                .items(null)
                .build();

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderDetailResponseDTO result = orderService.getOrderDetailById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        assertThat(result.getTotalItems()).isZero();
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        String nonExistentId = "non-existent";
        when(orderRepository.findByIdWithItems(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderDetailById(nonExistentId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Đơn hàng yêu cầu không tồn tại");
    }

    @Test
    void shouldAdvanceOrderStatusToNextAllowedStatus() {
        String orderId = "order-status-1";
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderDetailResponseDTO response = orderService.updateOrderStatus(orderId, OrderStatus.PROCESSING);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldRejectSkippingOrderStatusTransition() {
        String orderId = "order-status-skip";
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("Không thể chuyển trạng thái đơn hàng");
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldRestockInventoryWhenOrderStatusChangesToCancelled() {
        String orderId = "order-status-cancel";
        Product product = Product.builder()
                .id("prod-status-cancel")
                .stockQuantity(4)
                .soldCount(6)
                .build();
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PROCESSING)
                .items(new ArrayList<>())
                .build();
        OrderItem item = OrderItem.builder()
                .id("item-status-cancel")
                .order(order)
                .product(product)
                .quantity(2)
                .build();
        order.setItems(new ArrayList<>(List.of(item)));
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderDetailResponseDTO response = orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getStockQuantity()).isEqualTo(6);
        assertThat(product.getSoldCount()).isEqualTo(4);
        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldTreatCurrentOrderStatusAsIdempotent() {
        String orderId = "order-status-current";
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.CANCELLED)
                .items(new ArrayList<>())
                .build();
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderDetailResponseDTO response = orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, never()).save(order);
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldReleaseAllInventoryWhenOrderIsCancelled() {
        String orderId = "order-cancel-test";
        Order order = Order.builder()
                .id(orderId)
                .orderCode("ORD26000095")
                .status(OrderStatus.PENDING)
                .build();

        Product product = Product.builder()
                .id("prod-400")
                .stockQuantity(10)
                .soldCount(5)
                .build();

        OrderItem item = OrderItem.builder()
                .id("item-400")
                .product(product)
                .quantity(3)
                .unitPrice(new BigDecimal("100000"))
                .subtotal(new BigDecimal("300000"))
                .build();

        order.setItems(new ArrayList<>(List.of(item)));

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderDetailResponseDTO response = orderService.cancelOrder(orderId);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getStockQuantity()).isEqualTo(13); // 10 + 3
        assertThat(product.getSoldCount()).isEqualTo(2); // 5 - 3
        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }
}
