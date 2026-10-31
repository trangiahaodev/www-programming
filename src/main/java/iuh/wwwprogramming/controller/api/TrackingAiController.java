package iuh.wwwprogramming.controller.api;

import iuh.wwwprogramming.service.AiAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingAiController {

    private final AiAdvisorService aiAdvisorService;

    @GetMapping(value = "/ai-advice-quick", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, String>> advice(
            @RequestParam(defaultValue = "0") int clickCount,
            @RequestParam(defaultValue = "0") int dwellTimeSeconds,
            @RequestParam(defaultValue = "") String lastProductName) {
        AiAdvisorService.TrackingContext context = new AiAdvisorService.TrackingContext(
                Math.max(0, clickCount), Math.max(0, dwellTimeSeconds), lastProductName);
        return ResponseEntity.ok(Map.of("advice", aiAdvisorService.advise(context)));
    }
}
