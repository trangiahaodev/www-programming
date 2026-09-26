package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderItemQuantityUpdateDTO;
import iuh.wwwprogramming.dto.OrderQuantityUpdateRequestDTO;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderItem;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.exception.InvalidOrderItemException;
import iuh.wwwprogramming.exception.InvalidOrderStatusException;
import iuh.wwwprogramming.exception.OrderItemNotFoundException;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.repository.OrderItemRepository;
import iuh.wwwprogramming.repository.OrderRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderItemRepository);
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
    void shouldUpdateOrderQuantitiesAndRecalculateTotalsSuccessfully() {
        String orderId = "order-update-1";
        Order order = Order.builder()
                .id(orderId)
                .orderCode("ORD26000001")
                .customerName("Nguyễn Thị Lan")
                .customerPhone("0987654321")
                .shippingAddress("12 Nguyễn Văn Bảo")
                .totalAmount(new BigDecimal("650000.00"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
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

        OrderQuantityUpdateRequestDTO request = OrderQuantityUpdateRequestDTO.builder()
                .orderId(orderId)
                .items(List.of(
                        OrderItemQuantityUpdateDTO.builder().orderItemId("item-1").quantity(3).build(),
                        OrderItemQuantityUpdateDTO.builder().orderItemId("item-2").quantity(2).build()
                ))
                .build();

        OrderDetailResponseDTO response = orderService.updateOrderQuantities(orderId, request);

        assertThat(response).isNotNull();
        assertThat(item1.getQuantity()).isEqualTo(3);
        assertThat(item1.getSubtotal()).isEqualByComparingTo(new BigDecimal("600000.00"));
        assertThat(item2.getQuantity()).isEqualTo(2);
        assertThat(item2.getSubtotal()).isEqualByComparingTo(new BigDecimal("500000.00"));
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("1100000.00"));
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("1100000.00"));
        assertThat(response.getTotalItems()).isEqualTo(5);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowInvalidOrderStatusExceptionWhenOrderIsDelivered() {
        String orderId = "order-delivered";
        Order order = Order.builder()
                .id(orderId)
                .orderCode("ORD26000002")
                .customerName("Lê Văn C")
                .totalAmount(new BigDecimal("500000.00"))
                .status(OrderStatus.DELIVERED)
                .items(new ArrayList<>(List.of(
                        OrderItem.builder().id("item-1").unitPrice(new BigDecimal("100000.00")).quantity(5).subtotal(new BigDecimal("500000.00")).build()
                )))
                .build();

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));

        OrderQuantityUpdateRequestDTO request = OrderQuantityUpdateRequestDTO.builder()
                .orderId(orderId)
                .items(List.of(OrderItemQuantityUpdateDTO.builder().orderItemId("item-1").quantity(2).build()))
                .build();

        assertThatThrownBy(() -> orderService.updateOrderQuantities(orderId, request))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("không được phép thay đổi số lượng sản phẩm");
    }

    @Test
    void shouldThrowInvalidOrderItemExceptionWhenItemBelongsToDifferentOrder() {
        String orderId = "order-1";
        Order order = Order.builder()
                .id(orderId)
                .orderCode("ORD26000001")
                .status(OrderStatus.PROCESSING)
                .items(new ArrayList<>(List.of(
                        OrderItem.builder().id("item-1").unitPrice(new BigDecimal("100000.00")).quantity(1).subtotal(new BigDecimal("100000.00")).build()
                )))
                .build();

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.existsById("item-malicious")).thenReturn(true);

        OrderQuantityUpdateRequestDTO request = OrderQuantityUpdateRequestDTO.builder()
                .orderId(orderId)
                .items(List.of(OrderItemQuantityUpdateDTO.builder().orderItemId("item-malicious").quantity(5).build()))
                .build();

        assertThatThrownBy(() -> orderService.updateOrderQuantities(orderId, request))
                .isInstanceOf(InvalidOrderItemException.class)
                .hasMessageContaining("Mặt hàng cập nhật không thuộc về đơn hàng này");
    }

    @Test
    void shouldThrowOrderItemNotFoundExceptionWhenItemNotFoundAnywhere() {
        String orderId = "order-1";
        Order order = Order.builder()
                .id(orderId)
                .orderCode("ORD26000001")
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>(List.of(
                        OrderItem.builder().id("item-1").unitPrice(new BigDecimal("100000.00")).quantity(1).subtotal(new BigDecimal("100000.00")).build()
                )))
                .build();

        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.existsById("item-ghost")).thenReturn(false);

        OrderQuantityUpdateRequestDTO request = OrderQuantityUpdateRequestDTO.builder()
                .orderId(orderId)
                .items(List.of(OrderItemQuantityUpdateDTO.builder().orderItemId("item-ghost").quantity(3).build()))
                .build();

        assertThatThrownBy(() -> orderService.updateOrderQuantities(orderId, request))
                .isInstanceOf(OrderItemNotFoundException.class)
                .hasMessageContaining("Không tìm thấy mặt hàng cần cập nhật");
    }
}

