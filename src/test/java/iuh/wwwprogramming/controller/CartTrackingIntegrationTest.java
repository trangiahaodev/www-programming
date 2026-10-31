package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.CartService;
import iuh.wwwprogramming.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartTrackingIntegrationTest {

    @Mock
    private CartService cartService;

    @Mock
    private TrackingService trackingService;

    @Mock
    private UserRepository userRepository;

    private CartController cartController;

    @BeforeEach
    void setUp() {
        cartController = new CartController(cartService, trackingService, userRepository);
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng kích hoạt Cart Hook ngầm lưu sự kiện ADD_TO_CART")
    void testAddToCart_TriggersTrackingHook() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        AddToCartRequestDTO dto = AddToCartRequestDTO.builder()
                .productId("p-101")
                .quantity(2)
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "addToCartRequest");

        String view = cartController.addToCart(
                dto,
                bindingResult,
                "add",
                null,
                request,
                session,
                redirectAttributes
        );

        assertEquals("redirect:/cart", view);
        verify(cartService, times(1)).addToCart(eq(session), eq(dto));
        verify(trackingService, times(1)).recordCartEvent(
                eq(session.getId()),
                eq("p-101"),
                eq(2),
                eq(false),
                isNull()
        );
    }

    @Test
    @DisplayName("Bấm Mua Ngay kích hoạt Cart Hook ngầm với isBuyNow=true và chuyển hướng checkout")
    void testBuyNow_TriggersTrackingHookWithBuyNow() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        AddToCartRequestDTO dto = AddToCartRequestDTO.builder()
                .productId("p-202")
                .quantity(1)
                .build();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "addToCartRequest");

        String view = cartController.addToCart(
                dto,
                bindingResult,
                "buyNow",
                null,
                request,
                session,
                redirectAttributes
        );

        assertEquals("redirect:/checkout", view);
        verify(cartService, times(1)).addToCart(eq(session), eq(dto));
        verify(trackingService, times(1)).recordCartEvent(
                eq(session.getId()),
                eq("p-202"),
                eq(1),
                eq(true),
                isNull()
        );
    }
}
