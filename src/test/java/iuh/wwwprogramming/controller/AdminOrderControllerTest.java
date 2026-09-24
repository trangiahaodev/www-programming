package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.service.OrderService;
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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminOrderControllerTest {

    @Mock
    private OrderService orderService;

    private AdminOrderController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminOrderController(orderService);
    }

    @Test
    void shouldRejectInvalidDateRangeAndSkipServiceCall() {
        OrderFilterDTO filter = OrderFilterDTO.builder()
                .fromDate(LocalDate.of(2026, 9, 25))
                .toDate(LocalDate.of(2026, 9, 24))
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(filter, "filter");
        Model model = new ExtendedModelMap();

        String view = controller.listOrders(filter, bindingResult, model);

        assertThat(view).isEqualTo("admin/order-list");
        assertThat(bindingResult.hasErrors()).isTrue();
        assertThat(bindingResult.getGlobalError()).isNotNull();
        assertThat(bindingResult.getGlobalError().getDefaultMessage())
                .isEqualTo("Khoảng thời gian không hợp lệ: Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
        verifyNoInteractions(orderService);
    }

    @Test
    void shouldUseSafePagingValuesAndDeterministicSort() {
        OrderFilterDTO filter = OrderFilterDTO.builder()
                .keyword(" ORD ")
                .status(OrderStatus.PENDING)
                .page(-5)
                .size(999)
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(filter, "filter");
        Model model = new ExtendedModelMap();
        Page<OrderResponseDTO> resultPage = new PageImpl<>(List.of());

        when(orderService.getOrders(eq(filter), any(Pageable.class))).thenReturn(resultPage);

        String view = controller.listOrders(filter, bindingResult, model);

        assertThat(view).isEqualTo("admin/order-list");
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderService).getOrders(eq(filter), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("createdAt").isDescending()).isTrue();
        assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("id").isDescending()).isTrue();
    }

    @Test
    void shouldReloadLastPageWhenRequestedPageIsOutOfBounds() {
        OrderFilterDTO filter = OrderFilterDTO.builder()
                .page(9)
                .size(10)
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(filter, "filter");
        Model model = new ExtendedModelMap();

        Page<OrderResponseDTO> firstPageResult = new PageImpl<>(
                List.of(),
                PageRequest.of(9, 10),
                15
        );
        Page<OrderResponseDTO> lastPageResult = new PageImpl<>(
                List.of(OrderResponseDTO.builder().orderCode("ORD001").build()),
                PageRequest.of(1, 10),
                15
        );

        when(orderService.getOrders(eq(filter), any(Pageable.class)))
                .thenReturn(firstPageResult)
                .thenReturn(lastPageResult);

        String view = controller.listOrders(filter, bindingResult, model);

        assertThat(view).isEqualTo("admin/order-list");
        assertThat(model.getAttribute("currentPage")).isEqualTo(1);
        assertThat(model.getAttribute("totalPages")).isEqualTo(2);
        assertThat(filter.getPage()).isEqualTo(1);
        verify(orderService, times(2)).getOrders(eq(filter), any(Pageable.class));
    }
}
