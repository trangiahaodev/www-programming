package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "categories",
    indexes = {
            @Index(name = "idx_categories_code", columnList = "category_code"),
            @Index(name = "idx_categories_name", columnList = "name")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Category {

    // 1. Surrogate Key
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    // 2. Business Key
    @Column(name = "category_code", unique = true, updatable = false, length = 20, nullable = false)
    private String categoryCode;

    @org.hibernate.annotations.Nationalized
    @Column(nullable = false, length = 150, columnDefinition = "NVARCHAR(150)")
    private String name;

    @org.hibernate.annotations.Nationalized
    @Column(length = 500, columnDefinition = "NVARCHAR(500)")
    private String description;

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
