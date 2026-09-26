package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_code", columnList = "order_code"),
                @Index(name = "idx_orders_status", columnList = "status"),
                @Index(name = "idx_orders_created_at", columnList = "created_at")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Order {

    // 1. Surrogate Key (Khóa hệ thống)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    // 2. Business Key (Khóa nghiệp vụ)
    @Column(name = "order_code", unique = true, updatable = false, length = 20, nullable = false)
    private String orderCode;

    // TÍCH HỢP V2: Liên kết với tài khoản đặt hàng (Cho phép null để hỗ trợ khách vãng lai và DataInitializer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    // GIỮ V1: Lưu thông tin người nhận (Độc lập với User để hỗ trợ mua hộ/đổi địa chỉ)
    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    @Column(length = 500)
    private String note;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod = "COD";

    // GIỮ V1: Trạng thái thanh toán (Rất cần thiết cho E-commerce)
    @Builder.Default
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus = "UNPAID";

    // GIỮ V1: Dùng Enum an toàn hơn String
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // GIỮ V1: Mapping với OrderItem (Đồng bộ với DataInitializer)
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderItem> items = new ArrayList<>();
}