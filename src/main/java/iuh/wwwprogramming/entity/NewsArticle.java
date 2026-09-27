package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "news_articles",
    indexes = {
        @Index(name = "idx_articles_slug", columnList = "slug"),
        @Index(name = "idx_articles_category", columnList = "category"),
        @Index(name = "idx_articles_active", columnList = "active")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @org.hibernate.annotations.Nationalized
    @Column(nullable = false, length = 255, columnDefinition = "NVARCHAR(255)")
    private String title;

    @org.hibernate.annotations.Nationalized
    @Column(length = 1000, columnDefinition = "NVARCHAR(1000)")
    private String excerpt;

    @org.hibernate.annotations.Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "published_date", length = 30)
    private String publishedDate;

    @org.hibernate.annotations.Nationalized
    @Column(length = 100, columnDefinition = "NVARCHAR(100)")
    private String category;

    @org.hibernate.annotations.Nationalized
    @Column(length = 100, columnDefinition = "NVARCHAR(100)")
    private String author;

    @org.hibernate.annotations.Nationalized
    @Column(name = "author_role", length = 100, columnDefinition = "NVARCHAR(100)")
    private String authorRole;

    @Column(name = "read_time", length = 30)
    private String readTime;

    @Column(name = "image_url", length = 255)
    private String image;

    @Column(name = "linked_product_ids", length = 255)
    private String linkedProductIds;

    @Column(name = "tags", length = 255)
    private String tags;

    @Builder.Default
    @Column(name = "views_count")
    private Long viewsCount = 0L;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

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
