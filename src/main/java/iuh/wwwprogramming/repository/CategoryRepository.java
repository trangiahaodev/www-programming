package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("""
        SELECT new iuh.wwwprogramming.dto.CategoryResponseDTO(
            c.id,
            c.categoryCode,
            c.name,
            c.description,
            c.active,
            0L,
            c.createdAt
        )
        FROM Category c
        WHERE (:keyword IS NULL OR :keyword = ''
               OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.categoryCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<CategoryResponseDTO> searchCategories(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByCategoryCode(String categoryCode);

    boolean existsByName(String name);
}
