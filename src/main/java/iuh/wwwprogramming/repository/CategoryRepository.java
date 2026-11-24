package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query(
        value = """
            SELECT c FROM Category c
            WHERE (c.isDeleted = false OR c.isDeleted IS NULL)
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(c.categoryCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
        countQuery = """
            SELECT COUNT(c) FROM Category c
            WHERE (c.isDeleted = false OR c.isDeleted IS NULL)
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(c.categoryCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """
    )
    Page<Category> searchCategories(@Param("keyword") String keyword, Pageable pageable);

    Optional<Category> findByIdAndIsDeletedFalse(String id);

    boolean existsByCategoryCode(String categoryCode);

    boolean existsByCategoryCodeAndIsDeletedFalse(String categoryCode);

    boolean existsByName(String name);

    boolean existsByNameAndIsDeletedFalse(String name);

    boolean existsByNameAndIdNot(String name, String id);

    boolean existsByNameAndIdNotAndIsDeletedFalse(String name, String id);

    long countByIsDeletedFalse();

    @Query("SELECT c FROM Category c WHERE c.active = true AND (c.isDeleted = false OR c.isDeleted IS NULL) ORDER BY c.name ASC")
    List<Category> findByActiveTrueOrderByNameAsc();

    List<Category> findByActiveTrueAndIsDeletedFalseOrderByNameAsc();
}
