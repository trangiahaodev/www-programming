package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.CartService;
import iuh.wwwprogramming.service.CategoryService;
import iuh.wwwprogramming.service.HomeContentService;
import iuh.wwwprogramming.service.ProductService;
import iuh.wwwprogramming.service.TrackingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorefrontCartHandoffTest {

    @Mock
    private ProductService productService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private HomeContentService homeContentService;
    @Mock
    private CartService cartService;
    @Mock
    private TrackingService trackingService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("HomeController không còn tải dữ liệu Hệ thống chi nhánh (offices)")
    void homeControllerShouldNotLoadOffices() {
        HomeController controller = new HomeController(productService, categoryService, homeContentService);
        when(productService.getTopBrands()).thenReturn(List.of("Anessa"));
        when(productService.getFeaturedBrandProducts(4)).thenReturn(List.of());
        when(productService.getHotProducts(anyInt())).thenReturn(List.of());
        when(productService.getNewProducts(anyInt())).thenReturn(List.of());
        when(categoryService.getCategories(any(), any(Pageable.class))).thenReturn(Page.empty());
        when(homeContentService.getFeaturedNews(3)).thenReturn(List.of());
        when(homeContentService.getActiveVouchers(anyBoolean(), any(), anyBoolean())).thenReturn(List.of());
        when(homeContentService.getTotalActiveProducts()).thenReturn(20L);
        when(homeContentService.getTotalCategories()).thenReturn(6L);

        Model model = new ExtendedModelMap();
        String view = controller.home(null, false, "NEW", false, model);

        assertThat(view).isEqualTo("customer/home");
        assertThat(model.containsAttribute("offices")).isFalse();
        verify(homeContentService, never()).getOffices();
    }

    @Test
    @DisplayName("Nút Thêm vào giỏ từ Chi tiết SP truyền đúng productId và quantity sang CartService")
    void addToCartFromProductDetailShouldPassExactProductIdAndQuantity() {
        CartController controller = new CartController(cartService, trackingService, userRepository);
        AddToCartRequestDTO dto = AddToCartRequestDTO.builder()
                .productId("prod-uuid-001")
                .quantity(3)
                .build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "/san-pham/prod-uuid-001");
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.addToCart(
                dto,
                new BeanPropertyBindingResult(dto, "addToCartRequest"),
                "add",
                null,
                request,
                session,
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/san-pham/prod-uuid-001");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("successMessage");

        ArgumentCaptor<AddToCartRequestDTO> captor = ArgumentCaptor.forClass(AddToCartRequestDTO.class);
        verify(cartService).addToCart(eq(session), captor.capture());
        assertThat(captor.getValue().getProductId()).isEqualTo("prod-uuid-001");
        assertThat(captor.getValue().getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("Nút Mua ngay từ Chi tiết SP truyền đúng productId và quantity rồi điều hướng tới /checkout")
    void buyNowFromProductDetailShouldPassExactDataAndRedirectToCheckout() {
        CartController controller = new CartController(cartService, trackingService, userRepository);
        AddToCartRequestDTO dto = AddToCartRequestDTO.builder()
                .productId("prod-uuid-002")
                .quantity(2)
                .build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "/san-pham/prod-uuid-002");
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.addToCart(
                dto,
                new BeanPropertyBindingResult(dto, "addToCartRequest"),
                "buyNow",
                null,
                request,
                session,
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/checkout");
        ArgumentCaptor<AddToCartRequestDTO> captor = ArgumentCaptor.forClass(AddToCartRequestDTO.class);
        verify(cartService).addToCart(eq(session), captor.capture());
        assertThat(captor.getValue().getProductId()).isEqualTo("prod-uuid-002");
        assertThat(captor.getValue().getQuantity()).isEqualTo(2);
    }
}
