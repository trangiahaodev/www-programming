package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.dto.CartDTO;
import iuh.wwwprogramming.dto.CheckoutRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.dto.UpdateCartItemRequestDTO;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.impl.CartServiceImpl;
import iuh.wwwprogramming.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartAndCheckoutServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private CartService cartService;
    private OrderService orderService;
    private MockHttpSession session;

    private Product testProduct;
    private User testUser;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(productRepository);
        orderService = new OrderServiceImpl(orderRepository, productRepository, userRepository, cartService);
        session = new MockHttpSession();

        Category category = Category.builder()
                .id("cat-uuid-01")
                .categoryCode("CAT01")
                .name("Mỹ phẩm")
                .build();

        testProduct = Product.builder()
                .id("prod-uuid-01")
                .productCode("SP26000001")
                .name("Serum Vitamin C")
                .price(new BigDecimal("300000"))
                .stockQuantity(10)
                .active(true)
                .category(category)
                .build();

        testUser = User.builder()
                .id("user-uuid-01")
                .userCode("KH26000001")
                .email("khachhang@pinkycloud.com")
                .fullName("Nguyễn Thu Hà")
                .phone("0912345678")
                .address("12 Nguyễn Văn Bảo")
                .role("ROLE_CUSTOMER")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Test 1: Thêm sản phẩm vào giỏ hàng Session thành công")
    void testAddToCart_Success() {
        when(productRepository.findByIdAndActiveTrue("prod-uuid-01")).thenReturn(Optional.of(testProduct));

        AddToCartRequestDTO request = AddToCartRequestDTO.builder()
                .productId("prod-uuid-01")
                .quantity(2)
                .build();

        cartService.addToCart(session, request);

        CartDTO cart = cartService.getCart(session);
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getTotalQuantity());
        assertEquals(new BigDecimal("600000"), cart.getTotalAmount());
    }

    @Test
    @DisplayName("Test 2: Thêm số lượng vượt quá tồn kho phải ném ngoại lệ")
    void testAddToCart_ExceedStock_ThrowsException() {
        when(productRepository.findByIdAndActiveTrue("prod-uuid-01")).thenReturn(Optional.of(testProduct));

        AddToCartRequestDTO request = AddToCartRequestDTO.builder()
                .productId("prod-uuid-01")
                .quantity(15) // Tồn kho chỉ có 10
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(session, request);
        });

        assertTrue(exception.getMessage().contains("vượt quá tồn kho"));
    }

    @Test
    @DisplayName("Test 3: Cập nhật số lượng và xóa sản phẩm khỏi giỏ")
    void testUpdateAndRemoveCartItem() {
        when(productRepository.findByIdAndActiveTrue("prod-uuid-01")).thenReturn(Optional.of(testProduct));

        // Thêm 2 món ban đầu
        cartService.addToCart(session, AddToCartRequestDTO.builder().productId("prod-uuid-01").quantity(2).build());

        // Cập nhật lên 5 món
        cartService.updateQuantity(session, UpdateCartItemRequestDTO.builder().productId("prod-uuid-01").quantity(5).build());
        assertEquals(5, cartService.getCart(session).getTotalQuantity());

        // Xóa món
        cartService.removeItem(session, "prod-uuid-01");
        assertTrue(cartService.getCart(session).isEmpty());
    }

    @Test
    @DisplayName("Test 4: Checkout thành công - Tạo Order, trừ tồn kho và xóa giỏ Session")
    void testPlaceOrder_Success() {
        when(productRepository.findByIdAndActiveTrue("prod-uuid-01")).thenReturn(Optional.of(testProduct));
        when(userRepository.findByEmail("khachhang@pinkycloud.com")).thenReturn(Optional.of(testUser));
        when(orderRepository.existsByOrderCode(any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId("order-uuid-99");
            return o;
        });

        // 1. Thêm 3 sản phẩm vào giỏ
        cartService.addToCart(session, AddToCartRequestDTO.builder().productId("prod-uuid-01").quantity(3).build());
        assertFalse(cartService.getCart(session).isEmpty());

        // 2. Điền form checkout
        CheckoutRequestDTO checkoutRequest = CheckoutRequestDTO.builder()
                .recipientName("Nguyễn Thu Hà")
                .recipientPhone("0912345678")
                .shippingAddress("12 Nguyễn Văn Bảo, Gò Vấp, TP.HCM")
                .paymentMethod("COD")
                .note("Giao giờ hành chính")
                .build();

        // 3. Thực hiện đặt hàng
        OrderResponseDTO response = orderService.placeOrder(session, "khachhang@pinkycloud.com", checkoutRequest);

        // 4. Kiểm tra kết quả
        assertNotNull(response);
        assertNotNull(response.getOrderCode());
        assertEquals(new BigDecimal("900000"), response.getTotalAmount());
        assertEquals("Nguyễn Thu Hà", response.getRecipientName());

        // Kiểm tra tồn kho đã bị trừ (10 - 3 = 7)
        assertEquals(7, testProduct.getStockQuantity());
        verify(productRepository, times(1)).save(testProduct);

        // Kiểm tra Order đã được lưu vào DB
        verify(orderRepository, times(1)).save(any(Order.class));

        // Kiểm tra giỏ hàng trên Session đã được làm sạch hoàn toàn
        assertTrue(cartService.getCart(session).isEmpty());
    }

    @Test
    @DisplayName("Test 5: Checkout khi giỏ hàng rỗng phải ném IllegalStateException")
    void testPlaceOrder_EmptyCart_ThrowsException() {
        CheckoutRequestDTO checkoutRequest = CheckoutRequestDTO.builder()
                .recipientName("Nguyễn Thu Hà")
                .recipientPhone("0912345678")
                .shippingAddress("12 Nguyễn Văn Bảo")
                .paymentMethod("COD")
                .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.placeOrder(session, "khachhang@pinkycloud.com", checkoutRequest);
        });

        assertTrue(exception.getMessage().contains("Giỏ hàng của bạn đang trống"));
    }
}
