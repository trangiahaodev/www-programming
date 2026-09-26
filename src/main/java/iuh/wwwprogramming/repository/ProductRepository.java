package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
// =========================================================================
    // 1. NHÓM HÀM CHO ADMIN (Bao gồm tất cả trạng thái, dùng cho CRUD)
    // =========================================================================

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


    // =========================================================================
    // 2. NHÓM HÀM CHO CUSTOMER (Bắt buộc kiểm tra active = true)
    // =========================================================================

    // ĐÃ ĐỔI TÊN: Tránh conflict với hàm search của Admin
    @Query(
            value = """
            SELECT p FROM Product p
            JOIN FETCH p.category c
            WHERE p.active = true
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:categoryId IS NULL OR :categoryId = '' OR :categoryId = 'all' 
                   OR c.id = :categoryId OR c.categoryCode = :categoryId OR LOWER(c.name) = LOWER(:categoryId))
        """,
            countQuery = """
            SELECT COUNT(p) FROM Product p
            JOIN p.category c
            WHERE p.active = true
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:categoryId IS NULL OR :categoryId = '' OR :categoryId = 'all' 
                   OR c.id = :categoryId OR c.categoryCode = :categoryId OR LOWER(c.name) = LOWER(:categoryId))
        """
    )
    Page<Product> searchCustomerProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            Pageable pageable
    );

    // ĐÃ ĐỔI TÊN: Tránh conflict với hàm findById của Admin
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id AND p.active = true")
    Optional<Product> findActiveByIdWithCategory(@Param("id") String id);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.productCode = :productCode AND p.active = true")
    Optional<Product> findByProductCodeWithCategory(@Param("productCode") String productCode);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE UPPER(p.productCode) IN :codes AND p.active = true")
    List<Product> findByProductCodeIn(@Param("codes") List<String> codes);

    @Query(
            value = """
            SELECT p FROM Product p
            JOIN FETCH p.category c
            WHERE p.active = true
              AND c.id = :categoryId
              AND p.id <> :excludeProductId
        """
    )
    List<Product> findRelatedProducts(
            @Param("categoryId") String categoryId,
            @Param("excludeProductId") String excludeProductId,
            Pageable pageable
    );

    @Query(
            value = """
            SELECT p FROM Product p
            JOIN FETCH p.category c
            WHERE p.active = true AND (p.isHot = true OR p.discount >= 15)
        """
    )
    List<Product> findHotProducts(Pageable pageable);

    @Query(
            value = """
            SELECT p FROM Product p
            JOIN FETCH p.category c
            WHERE p.active = true AND p.isNew = true
        """
    )
    List<Product> findNewProducts(Pageable pageable);

    @Query(
            value = """
            SELECT p FROM Product p
            JOIN FETCH p.category c
            WHERE p.active = true
              AND p.brand IN ('Anessa', 'Vichy', 'MartiDerm', 'L''Oreal Paris', 'La Roche-Posay', 'Skin1004', 'Cosrx', 'Torriden', 'CeraVe', '3CE', 'Maybelline')
        """
    )
    List<Product> findFeaturedBrandProducts(Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true ORDER BY p.brand ASC")
    List<String> findDistinctBrands();
}
