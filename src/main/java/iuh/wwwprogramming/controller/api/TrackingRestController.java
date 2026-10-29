package iuh.wwwprogramming.controller.api;

import iuh.wwwprogramming.dto.TrackingEventDTO;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.entity.UserActivityLog;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.TrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingRestController {

    private final TrackingService trackingService;
    private final UserRepository userRepository;

    /**
     * Nhận sự kiện tracking từ JavaScript của client (Click card, Dwell time xem sản phẩm, v.v.)
     * Hỗ trợ cả application/json và navigator.sendBeacon (* / *)
     */
    @PostMapping(value = "/event", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE})
    public ResponseEntity<Map<String, Object>> recordEvent(
            @RequestBody(required = false) TrackingEventDTO event,
            HttpSession session,
            Principal principal,
            HttpServletRequest request
    ) {
        if (event == null) {
            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("status", "error");
            errorResp.put("message", "Payload sự kiện không được để trống.");
            return ResponseEntity.badRequest().body(errorResp);
        }

        try {
            String sessionId = (session != null) ? session.getId() : request.getSession(true).getId();
            User currentUser = null;
            if (principal != null) {
                currentUser = userRepository.findByEmail(principal.getName()).orElse(null);
            }

            UserActivityLog savedLog = trackingService.recordTrackingEvent(sessionId, event, currentUser);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "recorded");
            response.put("sessionId", sessionId);
            response.put("eventType", savedLog.getEventType());
            response.put("logId", savedLog.getId());
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.warn("Không thể ghi nhận sự kiện tracking: {}", ex.getMessage());
            Map<String, Object> fallbackResp = new HashMap<>();
            fallbackResp.put("status", "fallback");
            fallbackResp.put("message", ex.getMessage());
            return ResponseEntity.ok(fallbackResp);
        }
    }

    /**
     * Lấy danh sách lịch sử hành vi của phiên hiện tại (phục vụ Trợ lý AI và debug)
     */
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSessionActivities(HttpSession session) {
        String sessionId = (session != null) ? session.getId() : "";
        List<UserActivityLog> activities = trackingService.getSessionActivities(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("count", activities.size());
        response.put("activities", activities);
        return ResponseEntity.ok(response);
    }
}
