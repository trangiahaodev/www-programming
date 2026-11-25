package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.NewsArticle;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    @Query("SELECT a FROM NewsArticle a WHERE a.slug = :slug AND a.active = true AND (a.isDeleted = false OR a.isDeleted IS NULL)")
    Optional<NewsArticle> findBySlugAndActiveTrue(@Param("slug") String slug);

    Optional<NewsArticle> findBySlugAndActiveTrueAndIsDeletedFalse(String slug);

    Optional<NewsArticle> findByIdAndIsDeletedFalse(Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIsDeletedFalse(String slug);

    @Query("SELECT a FROM NewsArticle a WHERE a.active = true AND (a.isDeleted = false OR a.isDeleted IS NULL) ORDER BY a.publishedDate DESC")
    List<NewsArticle> findAllByActiveTrueOrderByPublishedDateDesc();

    List<NewsArticle> findAllByActiveTrueAndIsDeletedFalseOrderByPublishedDateDesc();

    @Query("SELECT a FROM NewsArticle a WHERE LOWER(a.category) = LOWER(:category) AND a.active = true AND (a.isDeleted = false OR a.isDeleted IS NULL)")
    List<NewsArticle> findByCategoryIgnoreCaseAndActiveTrue(@Param("category") String category);

    @Query("SELECT a FROM NewsArticle a WHERE a.isFeatured = true AND a.active = true AND (a.isDeleted = false OR a.isDeleted IS NULL) ORDER BY a.id ASC")
    List<NewsArticle> findFeaturedActiveArticles(Pageable pageable);

    default Optional<NewsArticle> findFirstByIsFeaturedTrueAndActiveTrue() {
        return findFeaturedActiveArticles(PageRequest.of(0, 1)).stream().findFirst();
    }

    Optional<NewsArticle> findFirstByIsFeaturedTrueAndActiveTrueAndIsDeletedFalse();

    @Query("SELECT DISTINCT a.category FROM NewsArticle a WHERE a.active = true AND (a.isDeleted = false OR a.isDeleted IS NULL) AND a.category IS NOT NULL")
    List<String> findDistinctCategories();

    @Query("SELECT a FROM NewsArticle a WHERE a.active = true AND (a.isDeleted = false OR a.isDeleted IS NULL) " +
           "AND (:category IS NULL OR :category = '' OR :category = 'all' OR LOWER(a.category) = LOWER(:category)) " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.publishedDate DESC, a.id DESC")
    List<NewsArticle> searchArticles(@Param("category") String category, @Param("keyword") String keyword);
}
