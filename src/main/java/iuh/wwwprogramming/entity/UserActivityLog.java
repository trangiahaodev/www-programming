package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_activity_logs",
        indexes = {
                @Index(name = "idx_activity_session", columnList = "session_id"),
                @Index(name = "idx_activity_product", columnList = "product_id"),
                @Index(name = "idx_activity_occurred_at", columnList = "occurred_at"),
                @Index(name = "idx_activity_event_type", columnList = "event_type")
        }
)
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

    // Liên kết tài khoản người dùng (nếu đã đăng nhập) - Không dùng khóa ngoại vật lý ConstraintMode.NO_CONSTRAINT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(length = 50)
    private String action;

    // Chiều ghi chính xuống CSDL cho product_id
    @Column(name = "product_id", length = 36)
    private String productId;

    // Chiều đọc tham chiếu tới Product - Không dùng khóa ngoại vật lý ConstraintMode.NO_CONSTRAINT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

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

    @Column(name = "page_url", length = 255)
    private String pageUrl;

    @CreationTimestamp
    @Column(name = "occurred_at", updatable = false, nullable = false)
    private LocalDateTime occurredAt;

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
            if (this.productName == null || this.productName.isBlank()) {
                this.productName = product.getName();
            }
        }
    }
}
