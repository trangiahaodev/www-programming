package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderStatus;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository);
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
}
