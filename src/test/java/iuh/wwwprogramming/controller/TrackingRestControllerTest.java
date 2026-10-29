package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.controller.api.TrackingRestController;
import iuh.wwwprogramming.dto.TrackingEventDTO;
import iuh.wwwprogramming.entity.UserActivityLog;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingRestControllerTest {

    @Mock
    private TrackingService trackingService;

    @Mock
    private UserRepository userRepository;

    private TrackingRestController controller;

    @BeforeEach
    void setUp() {
        controller = new TrackingRestController(trackingService, userRepository);
    }

    @Test
    @DisplayName("API /api/tracking/event nhận dữ liệu hợp lệ và ghi log thành công")
    void testRecordEvent_Success() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();

        TrackingEventDTO dto = TrackingEventDTO.builder()
                .eventType("PRODUCT_CLICK")
                .action("CLICK_PRODUCT")
                .productId("p-1")
                .productName("Sữa rửa mặt")
                .build();

        UserActivityLog savedLog = UserActivityLog.builder()
                .id("log-uuid-1")
                .eventType("PRODUCT_CLICK")
                .sessionId(session.getId())
                .build();

        when(trackingService.recordTrackingEvent(eq(session.getId()), eq(dto), isNull()))
                .thenReturn(savedLog);

        ResponseEntity<Map<String, Object>> response = controller.recordEvent(dto, session, null, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("recorded", response.getBody().get("status"));
        assertEquals("PRODUCT_CLICK", response.getBody().get("eventType"));
        assertEquals("log-uuid-1", response.getBody().get("logId"));
    }

    @Test
    @DisplayName("API /api/tracking/event từ chối khi payload null")
    void testRecordEvent_NullPayload() {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Map<String, Object>> response = controller.recordEvent(null, session, null, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
    }

    @Test
    @DisplayName("API /api/tracking/session trả về danh sách hoạt động trong session")
    void testGetSessionActivities() {
        MockHttpSession session = new MockHttpSession();
        UserActivityLog log1 = UserActivityLog.builder().id("1").sessionId(session.getId()).build();

        when(trackingService.getSessionActivities(session.getId())).thenReturn(List.of(log1));

        ResponseEntity<Map<String, Object>> response = controller.getSessionActivities(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().get("count"));
    }
}
