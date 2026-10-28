package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.TopProductStatDTO;
import iuh.wwwprogramming.dto.TrackingEventDTO;
import iuh.wwwprogramming.entity.UserActivityLog;
import iuh.wwwprogramming.service.OrderService;
import iuh.wwwprogramming.service.PinkyAiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PinkyAiController {

    private final PinkyAiService pinkyAiService;
    private final OrderService orderService;

    @PostMapping(value = "/tracking/event", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> recordEvent(@RequestBody TrackingEventDTO event, HttpSession session) {
        String eventType = required(event.getEventType(), "eventType");
        int dwell = firstPositive(event.getDeltaSeconds(), event.getDurationSeconds(), event.getDwellTimeSeconds());
        int clickCount = event.getClickCount() == null ? 0 : Math.max(event.getClickCount(), 0);
        int addToCart = event.getAddToCartCount() == null
                ? ("ADD_TO_CART".equalsIgnoreCase(eventType) ? 1 : 0)
                : Math.max(event.getAddToCartCount(), 0);

        pinkyAiService.recordActivity(session.getId(), UserActivityLog.builder()
                .eventType(eventType)
                .action(event.getAction())
                .productId(event.getProductId())
                .productName(event.getProductName())
                .clickCount(clickCount)
                .dwellSeconds(dwell)
                .addToCartCount(addToCart)
                .build());
        return Map.of("status", "recorded");
    }

    @GetMapping("/tracking/ai-advice")
    public Map<String, String> analyzeSession(HttpSession session) {
        List<TopProductStatDTO> topProducts = orderService.getTopOrderedProducts(5);
        return Map.of("advice", pinkyAiService.analyzeSession(session.getId(), topProducts));
    }

    @GetMapping("/analytics/top-products")
    public List<TopProductStatDTO> topProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return orderService.getTopOrderedProducts(limit);
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " không được để trống.");
        }
        return value.trim();
    }

    private int firstPositive(Integer... values) {
        for (Integer value : values) {
            if (value != null && value > 0) {
                return value;
            }
        }
        return 0;
    }
}
