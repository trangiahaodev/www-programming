package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    // Giải quyết triệt để N+1 Query bằng JOIN FETCH khi đọc chi tiết đơn hàng
    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.user u " +
           "LEFT JOIN FETCH o.orderDetails od " +
           "LEFT JOIN FETCH od.product p " +
           "WHERE o.orderCode = :orderCode")
    Optional<Order> findWithDetailsByOrderCode(@Param("orderCode") String orderCode);

    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);
    boolean existsByUserId(String userId);
}
