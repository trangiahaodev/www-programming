package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.TopProductStatDTO;
import iuh.wwwprogramming.service.OrderService;
import iuh.wwwprogramming.service.PinkyAiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PinkyAiController {

    private final PinkyAiService pinkyAiService;
    private final OrderService orderService;

    @GetMapping(value = "/tracking/ai-advice", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, String>> analyzeSession(HttpSession session) {
        return ResponseEntity.ok(Map.of("advice", pinkyAiService.analyzeSession(session)));
    }

    @GetMapping("/analytics/top-products")
    public List<TopProductStatDTO> topProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return orderService.getTopOrderedProducts(limit);
    }
}
