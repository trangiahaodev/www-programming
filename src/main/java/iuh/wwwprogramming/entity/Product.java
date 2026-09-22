package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_code", columnList = "product_code"),
        @Index(name = "idx_products_name", columnList = "name"),
        @Index(name = "idx_products_brand", columnList = "brand"),
        @Index(name = "idx_products_category", columnList = "category_id")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product {

    // 1. Surrogate Key
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    // 2. Business Key
    @Column(name = "product_code", unique = true, updatable = false, length = 30, nullable = false)
    private String productCode;

    @org.hibernate.annotations.Nationalized
    @Column(nullable = false, length = 255, columnDefinition = "NVARCHAR(255)")
    private String name;

    @org.hibernate.annotations.Nationalized
    @Column(nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String brand;

    @Column(nullable = false)
    private Double price;

    @Builder.Default
    @Column(nullable = false)
    private Integer discount = 0; // % discount (e.g. 10, 15, 20)

    @Column(length = 255)
    private String image;

    @Builder.Default
    @Column(length = 10)
    private String currency = "VND";

    @org.hibernate.annotations.Nationalized
    @Column(length = 100, columnDefinition = "NVARCHAR(100)")
    private String origin;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String ingredients;

    @Column(name = "usage_instructions", columnDefinition = "NVARCHAR(MAX)")
    private String usageInstructions;

    @Builder.Default
    @Column(nullable = false)
    private Integer stock = 50;

    @Column(length = 50)
    private String barcode;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "is_hot", nullable = false)
    private Boolean isHot = false;

    @Builder.Default
    @Column(name = "is_new", nullable = false)
    private Boolean isNew = false;

    @Builder.Default
    private Double rating = 5.0;

    @Builder.Default
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Builder.Default
    @Column(name = "sold_count")
    private Integer soldCount = 0;

    // 3. Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
