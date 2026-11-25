package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.config.SecurityConfig;
import iuh.wwwprogramming.dto.CartDTO;
import iuh.wwwprogramming.dto.CartItemDTO;
import iuh.wwwprogramming.dto.CheckoutRequestDTO;
import iuh.wwwprogramming.dto.OrderItemResponseDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.security.ShopPrincipal;
import iuh.wwwprogramming.service.CartService;
import iuh.wwwprogramming.service.OrderService;
import iuh.wwwprogramming.service.TrackingService;
import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({CartController.class, CheckoutController.class})
@Import(SecurityConfig.class)
class CartCheckoutWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private TrackingService trackingService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ShopUserDetailsService shopUserDetailsService;

    private ShopPrincipal customerPrincipal;
    private User customerUser;
    private CartDTO populatedCart;

    @BeforeEach
    void setUp() {
        customerPrincipal = new ShopPrincipal(
                "user-01",
                "khachhang@pinkycloud.com",
                "Nguyễn Thu Hà",
                "hash",
                "ROLE_CUSTOMER",
                true
        );

        customerUser = User.builder()
                .id("user-01")
                .userCode("KH26000001")
                .email("khachhang@pinkycloud.com")
                .fullName("Nguyễn Thu Hà")
                .phone("0912345678")
                .address("12 Nguyễn Văn Bảo, Gò Vấp, TP.HCM")
                .role("ROLE_CUSTOMER")
                .active(true)
                .build();

        populatedCart = new CartDTO();
        populatedCart.addItem(CartItemDTO.builder()
                .productId("prod-01")
                .productCode("SP26000001")
                .productName("Kem dưỡng ẩm Laneige")
                .imageUrl("/IMG/anh11.png")
                .unitPrice(new BigDecimal("450000"))
                .quantity(2)
                .stockQuantity(10)
                .build());
    }

    @Test
    @DisplayName("GET /cart dùng customer-layout & customer.css khi giỏ hàng trống")
    void viewCart_Empty_UsesCustomerLayout() throws Exception {
        when(cartService.getCart(any())).thenReturn(new CartDTO());

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cart-view"))
                .andExpect(content().string(containsString("/css/customer.css")))
                .andExpect(content().string(not(containsString("/css/store.css"))))
                .andExpect(content().string(containsString("Giỏ hàng của bạn đang trống!")));
    }

    @Test
    @DisplayName("GET /cart hiển thị sản phẩm và nút Tiến hành thanh toán")
    void viewCart_WithItems_RendersCheckoutButton() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(CartService.CART_SESSION_KEY, populatedCart);
        when(cartService.getCart(any())).thenReturn(populatedCart);

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cart-view"))
                .andExpect(content().string(containsString("/css/customer.css")))
                .andExpect(content().string(not(containsString("/css/store.css"))))
                .andExpect(content().string(containsString("Kem dưỡng ẩm Laneige")))
                .andExpect(content().string(containsString("Tiến hành thanh toán")))
                .andExpect(content().string(containsString("href=\"/checkout\"")));
    }

    @Test
    @DisplayName("GET /checkout yêu cầu đăng nhập khách hàng")
    void showCheckout_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("GET /checkout hiển thị form checkout dùng customer-layout và nạp sẵn thông tin khách")
    void showCheckout_AuthenticatedWithCart_RendersForm() throws Exception {
        when(shopUserDetailsService.isCurrent(customerPrincipal)).thenReturn(true);
        when(cartService.getCart(any())).thenReturn(populatedCart);
        when(userRepository.findByEmail("khachhang@pinkycloud.com")).thenReturn(Optional.of(customerUser));

        mockMvc.perform(get("/checkout").with(user(customerPrincipal)))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/checkout-form"))
                .andExpect(content().string(containsString("/css/customer.css")))
                .andExpect(content().string(not(containsString("/css/store.css"))))
                .andExpect(content().string(containsString("Nguyễn Thu Hà")))
                .andExpect(content().string(containsString("0912345678")))
                .andExpect(content().string(containsString("Xác Nhận Đặt Hàng")));
    }

    @Test
    @DisplayName("POST /checkout validation fail trả về form kèm thông báo lỗi")
    void processCheckout_ValidationErrors_ReturnsForm() throws Exception {
        when(shopUserDetailsService.isCurrent(customerPrincipal)).thenReturn(true);
        when(cartService.getCart(any())).thenReturn(populatedCart);

        mockMvc.perform(post("/checkout")
                        .with(user(customerPrincipal))
                        .param("recipientName", "")
                        .param("recipientPhone", "123")
                        .param("shippingAddress", "")
                        .param("paymentMethod", "COD"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/checkout-form"))
                .andExpect(content().string(containsString("account-error")));

        verifyNoInteractions(orderService);
    }

    @Test
    @DisplayName("POST /checkout hợp lệ gọi OrderService.placeOrder và redirect /checkout/success")
    void processCheckout_Valid_PlacesOrderAndRedirects() throws Exception {
        when(shopUserDetailsService.isCurrent(customerPrincipal)).thenReturn(true);
        when(cartService.getCart(any())).thenReturn(populatedCart);

        OrderResponseDTO responseDTO = OrderResponseDTO.builder()
                .id("order-01")
                .orderCode("ORD26123456")
                .customerName("Nguyễn Thu Hà")
                .customerPhone("0912345678")
                .shippingAddress("12 Nguyễn Văn Bảo, Gò Vấp, TP.HCM")
                .paymentMethod("COD")
                .paymentStatus("UNPAID")
                .status(OrderStatus.PENDING)
                .statusDisplay("Chờ xác nhận")
                .totalItems(2)
                .totalAmount(new BigDecimal("900000"))
                .items(List.of(OrderItemResponseDTO.builder()
                        .productName("Kem dưỡng ẩm Laneige")
                        .quantity(2)
                        .unitPrice(new BigDecimal("450000"))
                        .subtotal(new BigDecimal("900000"))
                        .build()))
                .build();

        when(orderService.placeOrder(any(), eq("khachhang@pinkycloud.com"), any(CheckoutRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/checkout")
                        .with(user(customerPrincipal))
                        .param("recipientName", "Nguyễn Thu Hà")
                        .param("recipientPhone", "0912345678")
                        .param("shippingAddress", "12 Nguyễn Văn Bảo, Gò Vấp, TP.HCM")
                        .param("paymentMethod", "COD")
                        .param("note", "Giao giờ hành chính"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/success"))
                .andExpect(flash().attributeExists("order", "orderCode", "successMessage"));

        verify(orderService).placeOrder(any(), eq("khachhang@pinkycloud.com"), any(CheckoutRequestDTO.class));
    }

    @Test
    @DisplayName("GET /checkout/success render thông tin OrderResponseDTO không bị lỗi SpEL")
    void orderSuccess_WithOrderCode_RendersOrderDetails() throws Exception {
        when(shopUserDetailsService.isCurrent(customerPrincipal)).thenReturn(true);

        OrderResponseDTO responseDTO = OrderResponseDTO.builder()
                .id("order-01")
                .orderCode("ORD26123456")
                .customerName("Nguyễn Thu Hà")
                .customerPhone("0912345678")
                .shippingAddress("12 Nguyễn Văn Bảo, Gò Vấp, TP.HCM")
                .paymentMethod("COD")
                .status(OrderStatus.PENDING)
                .statusDisplay("Chờ xác nhận")
                .totalItems(2)
                .totalAmount(new BigDecimal("900000"))
                .items(List.of(OrderItemResponseDTO.builder()
                        .productName("Kem dưỡng ẩm Laneige")
                        .quantity(2)
                        .unitPrice(new BigDecimal("450000"))
                        .subtotal(new BigDecimal("900000"))
                        .build()))
                .build();

        when(orderService.getOrderByCode("ORD26123456")).thenReturn(responseDTO);

        mockMvc.perform(get("/checkout/success")
                        .param("orderCode", "ORD26123456")
                        .with(user(customerPrincipal)))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/order-success"))
                .andExpect(content().string(containsString("/css/customer.css")))
                .andExpect(content().string(not(containsString("/css/store.css"))))
                .andExpect(content().string(containsString("ORD26123456")))
                .andExpect(content().string(containsString("Nguyễn Thu Hà")))
                .andExpect(content().string(containsString("0912345678")))
                .andExpect(content().string(containsString("Chờ xác nhận")))
                .andExpect(content().string(containsString("Kem dưỡng ẩm Laneige")));
    }
}
