package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.exception.InvalidOrderStatusException;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

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
        verify(orderService, times(1)).getOrders(eq(filter), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("createdAt").isDescending()).isTrue();
        assertThat(pageable.getSort().getOrderFor("id")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("id").isDescending()).isTrue();
    }

    @Test
    void shouldRedirectWhenOrderIdIsBlank() {
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.viewOrderDetail("   ", redirectAttributes, model);

        assertThat(view).isEqualTo("redirect:/admin/orders");
        assertThat(redirectAttributes.getFlashAttributes().get("errorMessage"))
                .isEqualTo("Mã đơn hàng không được để trống.");
        verifyNoInteractions(orderService);
    }

    @Test
    void shouldRedirectWhenOrderIdFormatIsInvalid() {
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.viewOrderDetail("not-a-valid-uuid", redirectAttributes, model);

        assertThat(view).isEqualTo("redirect:/admin/orders");
        assertThat(redirectAttributes.getFlashAttributes().get("errorMessage"))
                .isEqualTo("Mã đơn hàng không đúng định dạng.");
        verifyNoInteractions(orderService);
    }

    @Test
    void shouldRenderOrderDetailViewWhenOrderExists() {
        String validUuid = "a3c4db73-b25d-4662-b49e-6b5b696e99fb";
        OrderDetailResponseDTO detailDTO = OrderDetailResponseDTO.builder()
                .id(validUuid)
                .orderCode("ORD26000001")
                .customerName("Nguyễn Thị Lan")
                .status(OrderStatus.PENDING)
                .build();

        when(orderService.getOrderDetailById(validUuid)).thenReturn(detailDTO);

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.viewOrderDetail(validUuid, redirectAttributes, model);

        assertThat(view).isEqualTo("admin/order-detail");
        assertThat(model.getAttribute("order")).isEqualTo(detailDTO);
        assertThat(model.containsAttribute("updateRequest")).isFalse();
        assertThat(model.getAttribute("availableOrderStatuses"))
                .isEqualTo(List.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED));
        verify(orderService).getOrderDetailById(validUuid);
    }

    @Test
    void shouldUpdateOrderStatusAndRedirectWithSuccessMessage() {
        String validUuid = "a3c4db73-b25d-4662-b49e-6b5b696e99fb";
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        when(orderService.updateOrderStatus(validUuid, OrderStatus.PROCESSING))
                .thenReturn(OrderDetailResponseDTO.builder().id(validUuid).status(OrderStatus.PROCESSING).build());

        String view = controller.updateOrderStatus(validUuid, OrderStatus.PROCESSING, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/orders/" + validUuid);
        assertThat(redirectAttributes.getFlashAttributes().get("successMessage"))
                .isEqualTo("Trạng thái đơn hàng đã được cập nhật.");
        verify(orderService).updateOrderStatus(validUuid, OrderStatus.PROCESSING);
    }

    @Test
    void shouldRedirectWithErrorWhenOrderStatusTransitionIsInvalid() {
        String validUuid = "a3c4db73-b25d-4662-b49e-6b5b696e99fb";
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        when(orderService.updateOrderStatus(validUuid, OrderStatus.SHIPPED))
                .thenThrow(new InvalidOrderStatusException("Không thể chuyển trạng thái đơn hàng."));

        String view = controller.updateOrderStatus(validUuid, OrderStatus.SHIPPED, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/admin/orders/" + validUuid);
        assertThat(redirectAttributes.getFlashAttributes().get("errorMessage"))
                .isEqualTo("Không thể chuyển trạng thái đơn hàng.");
    }

    @Test
    void shouldRedirectToOrderListWhenOrderNotFound() {
        String validUuid = "00000000-0000-0000-0000-000000000000";
        when(orderService.getOrderDetailById(validUuid))
                .thenThrow(new OrderNotFoundException("Đơn hàng không tồn tại"));

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.viewOrderDetail(validUuid, redirectAttributes, model);

        assertThat(view).isEqualTo("redirect:/admin/orders");
        assertThat(redirectAttributes.getFlashAttributes().get("errorMessage"))
                .isEqualTo("Đơn hàng không tồn tại");
    }

}
