package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(
        value = """
            SELECT o FROM Order o
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR o.customerPhone LIKE CONCAT('%', :keyword, '%'))
              AND (:status IS NULL OR o.status = :status)
              AND (:startDateTime IS NULL OR o.createdAt >= :startDateTime)
              AND (:endDateTime IS NULL OR o.createdAt <= :endDateTime)
        """,
        countQuery = """
            SELECT COUNT(o) FROM Order o
            WHERE (:keyword IS NULL OR :keyword = ''
                   OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR o.customerPhone LIKE CONCAT('%', :keyword, '%'))
              AND (:status IS NULL OR o.status = :status)
              AND (:startDateTime IS NULL OR o.createdAt >= :startDateTime)
              AND (:endDateTime IS NULL OR o.createdAt <= :endDateTime)
        """
    )
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") String id);

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);
}
