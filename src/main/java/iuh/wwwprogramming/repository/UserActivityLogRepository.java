package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, String> {

    List<UserActivityLog> findBySessionIdOrderByOccurredAtAsc(String sessionId);

    @Query("""
            SELECT l.productName, SUM(l.clickCount), SUM(l.dwellSeconds), SUM(l.addToCartCount)
            FROM UserActivityLog l
            WHERE l.sessionId = :sessionId
            GROUP BY l.productName
            ORDER BY SUM(l.clickCount) DESC, SUM(l.dwellSeconds) DESC
            """)
    List<Object[]> summarizeSessionByProduct(@Param("sessionId") String sessionId);

    @Query("""
            SELECT COALESCE(SUM(l.clickCount), 0)
            FROM UserActivityLog l
            WHERE l.occurredAt >= :fromDate AND l.occurredAt < :toDate
            """)
    long sumClickCountBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("""
            SELECT COALESCE(SUM(l.addToCartCount), 0)
            FROM UserActivityLog l
            WHERE l.occurredAt >= :fromDate AND l.occurredAt < :toDate
            """)
    long sumAddToCartCountBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("""
            SELECT COALESCE(AVG(l.dwellSeconds), 0)
            FROM UserActivityLog l
            WHERE l.occurredAt >= :fromDate AND l.occurredAt < :toDate
            """)
    Double averageDwellSecondsBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("""
            SELECT l.productName, SUM(l.clickCount), SUM(l.addToCartCount)
            FROM UserActivityLog l
            WHERE l.occurredAt >= :fromDate AND l.occurredAt < :toDate
              AND l.productName IS NOT NULL AND l.productName <> ''
            GROUP BY l.productName
            ORDER BY SUM(l.clickCount) DESC, SUM(l.addToCartCount) DESC
            """)
    List<Object[]> findTopProductSignalsBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}
