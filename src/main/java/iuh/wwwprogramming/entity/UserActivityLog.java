package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_logs", indexes = {
        @Index(name = "idx_activity_session", columnList = "session_id"),
        @Index(name = "idx_activity_product", columnList = "product_id"),
        @Index(name = "idx_activity_occurred_at", columnList = "occurred_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "session_id", nullable = false, length = 128)
    private String sessionId;

    @Column(name = "event_type", nullable = false, length = 40)
    private String eventType;

    @Column(length = 40)
    private String action;

    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Integer clickCount = 0;

    @Column(name = "dwell_seconds", nullable = false)
    @Builder.Default
    private Integer dwellSeconds = 0;

    @Column(name = "add_to_cart_count", nullable = false)
    @Builder.Default
    private Integer addToCartCount = 0;

    @CreationTimestamp
    @Column(name = "occurred_at", updatable = false, nullable = false)
    private LocalDateTime occurredAt;
}
