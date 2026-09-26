package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query(
        value = """
            SELECT p FROM Product p
            JOIN FETCH p.category c
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:categoryId IS NULL OR :categoryId = '' OR c.id = :categoryId)
        """,
        countQuery = """
            SELECT COUNT(p) FROM Product p
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:categoryId IS NULL OR :categoryId = '' OR p.category.id = :categoryId)
        """
    )
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") String id);

    boolean existsByProductCode(String productCode);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, String id);

    long countByCategoryId(String categoryId);
}
