package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    Optional<NewsArticle> findBySlugAndActiveTrue(String slug);

    List<NewsArticle> findAllByActiveTrueOrderByPublishedDateDesc();

    List<NewsArticle> findByCategoryIgnoreCaseAndActiveTrue(String category);

    Optional<NewsArticle> findFirstByIsFeaturedTrueAndActiveTrue();

    @Query("SELECT DISTINCT a.category FROM NewsArticle a WHERE a.active = true AND a.category IS NOT NULL")
    List<String> findDistinctCategories();

    @Query("SELECT a FROM NewsArticle a WHERE a.active = true " +
           "AND (:category IS NULL OR :category = '' OR :category = 'all' OR LOWER(a.category) = LOWER(:category)) " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.category) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.publishedDate DESC, a.id DESC")
    List<NewsArticle> searchArticles(@Param("category") String category, @Param("keyword") String keyword);
}
