package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.TrackingEventDTO;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.entity.UserActivityLog;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.repository.UserActivityLogRepository;
import iuh.wwwprogramming.service.impl.TrackingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private UserActivityLogRepository activityLogRepository;

    @Mock
    private ProductRepository productRepository;

    private TrackingService trackingService;

    @BeforeEach
    void setUp() {
        trackingService = new TrackingServiceImpl(activityLogRepository, productRepository);
    }

    @Test
    @DisplayName("Ghi nhận sự kiện click sản phẩm thành công")
    void testRecordTrackingEvent_ProductClick() {
        TrackingEventDTO event = TrackingEventDTO.builder()
                .eventType("PRODUCT_CLICK")
                .action("CLICK_PRODUCT")
                .productId("p-123")
                .productName("Kem Dưỡng Da Pinky")
                .clickCount(3)
                .pageUrl("/san-pham/p-123")
                .build();

        when(activityLogRepository.save(any(UserActivityLog.class))).thenAnswer(inv -> inv.getArgument(0));

        UserActivityLog saved = trackingService.recordTrackingEvent("session-abc", event, null);

        assertNotNull(saved);
        assertEquals("session-abc", saved.getSessionId());
        assertEquals("PRODUCT_CLICK", saved.getEventType());
        assertEquals("CLICK_PRODUCT", saved.getAction());
        assertEquals("p-123", saved.getProductId());
        assertEquals("Kem Dưỡng Da Pinky", saved.getProductName());
        assertEquals(3, saved.getClickCount());
        assertEquals(0, saved.getDwellSeconds());
        assertEquals(0, saved.getAddToCartCount());

        verify(activityLogRepository, times(1)).save(any(UserActivityLog.class));
    }

    @Test
    @DisplayName("Ghi nhận sự kiện dwell time thành công với fallback deltaSeconds")
    void testRecordTrackingEvent_DwellTime() {
        TrackingEventDTO event = TrackingEventDTO.builder()
                .eventType("DWELL_TIME")
                .action("VIEW_PRODUCT_DETAIL")
                .productId("p-999")
                .deltaSeconds(15)
                .dwellTimeSeconds(30)
                .build();

        Product mockProduct = Product.builder().id("p-999").name("Serum Cấp Ẩm").build();
        when(productRepository.findById("p-999")).thenReturn(Optional.of(mockProduct));
        when(activityLogRepository.save(any(UserActivityLog.class))).thenAnswer(inv -> inv.getArgument(0));

        UserActivityLog saved = trackingService.recordTrackingEvent("sess-xyz", event, null);

        assertNotNull(saved);
        assertEquals("DWELL_TIME", saved.getEventType());
        assertEquals("Serum Cấp Ẩm", saved.getProductName());
        assertEquals(15, saved.getDwellSeconds());
    }

    @Test
    @DisplayName("Ghi nhận sự kiện Cart Hook khi khách bấm Thêm vào giỏ")
    void testRecordCartEvent_AddToCart() {
        Product mockProduct = Product.builder().id("prod-1").name("Son Dưỡng Hồng").build();
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(mockProduct));
        when(activityLogRepository.save(any(UserActivityLog.class))).thenAnswer(inv -> inv.getArgument(0));

        User mockUser = User.builder().id("u-1").email("customer@example.com").build();

        UserActivityLog saved = trackingService.recordCartEvent("sess-1", "prod-1", 2, false, mockUser);

        assertNotNull(saved);
        assertEquals("sess-1", saved.getSessionId());
        assertEquals("ADD_TO_CART", saved.getEventType());
        assertEquals("ADD_TO_CART", saved.getAction());
        assertEquals("prod-1", saved.getProductId());
        assertEquals("Son Dưỡng Hồng", saved.getProductName());
        assertEquals(2, saved.getAddToCartCount());
        assertEquals(1, saved.getClickCount());
        assertEquals("/cart", saved.getPageUrl());
        assertEquals(mockUser, saved.getUser());
    }

    @Test
    @DisplayName("Ghi nhận sự kiện Cart Hook khi khách bấm Mua ngay")
    void testRecordCartEvent_BuyNow() {
        Product mockProduct = Product.builder().id("prod-2").name("Kem Chống Nắng").build();
        when(productRepository.findById("prod-2")).thenReturn(Optional.of(mockProduct));
        when(activityLogRepository.save(any(UserActivityLog.class))).thenAnswer(inv -> inv.getArgument(0));

        UserActivityLog saved = trackingService.recordCartEvent("sess-2", "prod-2", 1, true, null);

        assertNotNull(saved);
        assertEquals("BUY_NOW", saved.getAction());
        assertEquals("ADD_TO_CART", saved.getEventType());
        assertEquals(1, saved.getAddToCartCount());
        assertEquals("/checkout", saved.getPageUrl());
    }

    @Test
    @DisplayName("Lấy danh sách tương tác theo session")
    void testGetSessionActivities() {
        UserActivityLog log1 = UserActivityLog.builder().id("1").sessionId("sess-1").build();
        when(activityLogRepository.findBySessionIdOrderByOccurredAtAsc("sess-1")).thenReturn(List.of(log1));

        List<UserActivityLog> result = trackingService.getSessionActivities("sess-1");
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }
}
