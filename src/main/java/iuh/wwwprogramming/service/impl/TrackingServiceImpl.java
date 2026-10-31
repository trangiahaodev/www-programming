package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.TrackingEventDTO;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.entity.UserActivityLog;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.repository.UserActivityLogRepository;
import iuh.wwwprogramming.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackingServiceImpl implements TrackingService {

    private final UserActivityLogRepository activityLogRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public UserActivityLog recordTrackingEvent(String sessionId, TrackingEventDTO eventDto, User user) {
        if (eventDto == null) {
            throw new IllegalArgumentException("Tracking event DTO cannot be null");
        }

        String safeSessionId = resolveSessionId(sessionId);
        String eventType = (eventDto.getEventType() != null && !eventDto.getEventType().isBlank())
                ? eventDto.getEventType().trim().toUpperCase()
                : "TRACKING_EVENT";

        String action = eventDto.getAction();
        if (action == null || action.isBlank()) {
            action = eventType;
        }

        int dwellSeconds = resolveDwellSeconds(eventDto);
        int clickCount = resolveClickCount(eventDto, eventType);
        int addToCartCount = resolveAddToCartCount(eventDto, eventType);

        String productId = eventDto.getProductId();
        String productName = eventDto.getProductName();

        if ((productName == null || productName.isBlank()) && productId != null && !productId.isBlank()) {
            productName = productRepository.findById(productId)
                    .map(Product::getName)
                    .orElse(null);
        }

        UserActivityLog activityLog = UserActivityLog.builder()
                .sessionId(safeSessionId)
                .user(user)
                .eventType(eventType)
                .action(action)
                .productId(productId)
                .productName(productName)
                .clickCount(clickCount)
                .dwellSeconds(dwellSeconds)
                .addToCartCount(addToCartCount)
                .pageUrl(eventDto.getPageUrl())
                .build();

        UserActivityLog saved = activityLogRepository.save(activityLog);
        log.debug("Recorded tracking event: type={}, product={}, dwell={}s, session={}",
                eventType, productName, dwellSeconds, safeSessionId);
        return saved;
    }

    @Override
    @Transactional
    public UserActivityLog recordCartEvent(String sessionId, String productId, int quantity, boolean isBuyNow, User user) {
        String safeSessionId = resolveSessionId(sessionId);
        String eventType = "ADD_TO_CART";
        String action = isBuyNow ? "BUY_NOW" : "ADD_TO_CART";
        int safeQuantity = Math.max(1, quantity);

        String productName = null;
        if (productId != null && !productId.isBlank()) {
            productName = productRepository.findById(productId)
                    .map(Product::getName)
                    .orElse(null);
        }

        UserActivityLog activityLog = UserActivityLog.builder()
                .sessionId(safeSessionId)
                .user(user)
                .eventType(eventType)
                .action(action)
                .productId(productId)
                .productName(productName)
                .clickCount(1)
                .dwellSeconds(0)
                .addToCartCount(safeQuantity)
                .pageUrl(isBuyNow ? "/checkout" : "/cart")
                .build();

        UserActivityLog saved = activityLogRepository.save(activityLog);
        log.info("Recorded cart hook event: action={}, product={}, qty={}, session={}",
                action, productName != null ? productName : productId, safeQuantity, safeSessionId);
        return saved;
    }

    @Override
    public List<UserActivityLog> getSessionActivities(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return List.of();
        }
        return activityLogRepository.findBySessionIdOrderByOccurredAtAsc(sessionId.trim());
    }

    private String resolveSessionId(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            return sessionId.trim();
        }
        return "anon-" + UUID.randomUUID();
    }

    private int resolveDwellSeconds(TrackingEventDTO dto) {
        if (dto.getDeltaSeconds() != null && dto.getDeltaSeconds() > 0) {
            return dto.getDeltaSeconds();
        }
        if (dto.getDwellTimeSeconds() != null && dto.getDwellTimeSeconds() > 0) {
            return dto.getDwellTimeSeconds();
        }
        if (dto.getDwellTime() != null && dto.getDwellTime() > 0) {
            return dto.getDwellTime();
        }
        if (dto.getDurationSeconds() != null && dto.getDurationSeconds() > 0) {
            return dto.getDurationSeconds();
        }
        return 0;
    }

    private int resolveClickCount(TrackingEventDTO dto, String eventType) {
        if (dto.getClickCount() != null) {
            return Math.max(0, dto.getClickCount());
        }
        return ("PRODUCT_CLICK".equalsIgnoreCase(eventType) || "CLICK_PRODUCT".equalsIgnoreCase(dto.getAction())) ? 1 : 0;
    }

    private int resolveAddToCartCount(TrackingEventDTO dto, String eventType) {
        if (dto.getAddToCartCount() != null) {
            return Math.max(0, dto.getAddToCartCount());
        }
        return ("ADD_TO_CART".equalsIgnoreCase(eventType) || "BUY_NOW".equalsIgnoreCase(dto.getAction())) ? 1 : 0;
    }
}
