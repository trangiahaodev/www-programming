package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_code", columnList = "user_code"),
        @Index(name = "idx_users_email", columnList = "email")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    // 1. Surrogate Key
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    // 2. Business Key
    @Column(name = "user_code", unique = true, updatable = false, length = 20, nullable = false)
    private String userCode;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Builder.Default
    @Column(nullable = false, length = 30)
    private String role = "ROLE_CUSTOMER";

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
