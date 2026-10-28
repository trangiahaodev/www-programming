package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrderId(String orderId);

    boolean existsByProductId(String productId);

    boolean existsByProductCode(String productCode);

    @Query(value = """
            SELECT oi.product_name, SUM(oi.quantity), COUNT(DISTINCT oi.order_id)
            FROM order_items oi
            INNER JOIN orders o ON o.id = oi.order_id
            WHERE o.status IS NULL OR o.status <> 'CANCELLED'
            GROUP BY oi.product_name
            ORDER BY SUM(oi.quantity) DESC, COUNT(DISTINCT oi.order_id) DESC
            """, nativeQuery = true)
    List<Object[]> findTopOrderedProducts(org.springframework.data.domain.Pageable pageable);

    @Query(value = """
            SELECT TOP (:limit)
                oi.product_name,
                COALESCE(p.brand, ''),
                COALESCE(p.image_url, ''),
                SUM(CASE WHEN o.created_at >= :fromDate AND o.created_at < :toDate THEN oi.quantity ELSE 0 END),
                COUNT(DISTINCT CASE WHEN o.created_at >= :fromDate AND o.created_at < :toDate THEN o.id END),
                COALESCE(SUM(CASE WHEN o.created_at >= :fromDate AND o.created_at < :toDate THEN oi.subtotal ELSE 0 END), 0),
                SUM(CASE WHEN o.created_at >= :previousFromDate AND o.created_at < :previousToDate THEN oi.quantity ELSE 0 END)
            FROM order_items oi
            INNER JOIN orders o ON o.id = oi.order_id
            LEFT JOIN products p ON p.id = oi.product_id
            WHERE (o.status IS NULL OR o.status <> 'CANCELLED')
              AND o.created_at >= :previousFromDate
              AND o.created_at < :toDate
            GROUP BY oi.product_name, p.brand, p.image_url
            HAVING SUM(CASE WHEN o.created_at >= :fromDate AND o.created_at < :toDate THEN oi.quantity ELSE 0 END) > 0
            ORDER BY
                SUM(CASE WHEN o.created_at >= :fromDate AND o.created_at < :toDate THEN oi.quantity ELSE 0 END) DESC,
                COALESCE(SUM(CASE WHEN o.created_at >= :fromDate AND o.created_at < :toDate THEN oi.subtotal ELSE 0 END), 0) DESC
            """, nativeQuery = true)
    List<Object[]> findOrderAnalytics(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("previousFromDate") LocalDateTime previousFromDate,
            @Param("previousToDate") LocalDateTime previousToDate,
            @Param("limit") int limit);
}
