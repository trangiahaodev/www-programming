package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);

    List<Product> findByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(String id);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findProductWithCategoryById(@Param("id") String id);
}
