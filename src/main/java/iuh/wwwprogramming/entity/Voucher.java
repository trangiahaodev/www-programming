package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "vouchers",
    indexes = {
        @Index(name = "idx_vouchers_code", columnList = "code"),
        @Index(name = "idx_vouchers_status", columnList = "status"),
        @Index(name = "idx_vouchers_active", columnList = "active")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @org.hibernate.annotations.Nationalized
    @Column(nullable = false, length = 255, columnDefinition = "NVARCHAR(255)")
    private String title;

    @org.hibernate.annotations.Nationalized
    @Column(length = 255, columnDefinition = "NVARCHAR(255)")
    private String detail;

    @Builder.Default
    @Column(length = 30)
    private String status = "active";

    @Column(length = 255)
    private String accent;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "min_order_amount", precision = 18, scale = 2)
    private BigDecimal minOrderAmount;

    @Builder.Default
    @Column(name = "usage_limit")
    private Integer usageLimit = 1000;

    @Builder.Default
    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
