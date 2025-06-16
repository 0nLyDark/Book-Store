package com.dangphuoctai.BookStore.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Order;
import com.dangphuoctai.BookStore.enums.OrderStatus;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

        @Query("SELECT o FROM Order o WHERE o.user.userId = :userId")
        Page<Order> findAllByUserId(Long userId, Pageable pageDetails);

        Optional<Order> findByEmail(String email);

        Optional<Order> findByOrderCode(String orderCode);

        List<Order> findByOrderStatusAndOrderDateTimeBefore(OrderStatus pending, LocalDateTime expiredTime);

        long countByOrderStatus(OrderStatus completed);

        @Query("SELECT COALESCE(SUM(" +
                        "o.subTotal " +
                        " - CASE " +
                        "     WHEN o.coupon IS NOT NULL THEN " +
                        "       CASE WHEN o.coupon.valueType = true THEN o.subTotal * o.coupon.value / 100 ELSE o.coupon.value END "
                        +
                        "     ELSE 0 " +
                        "   END" +
                        "), 0) " +
                        "FROM Order o " +
                        "WHERE o.orderStatus = :orderStatus")
        double getTotalRevenueByOrderStatus(OrderStatus orderStatus);

        @Query("SELECT FUNCTION('DATE_FORMAT', o.orderDateTime, '%d-%m-%Y'),SUM(o.subTotal) " +
                        "FROM Order o " +
                        "WHERE o.orderStatus = :status " +
                        "AND o.orderDateTime BETWEEN :startDate AND :endDate " +
                        "GROUP BY FUNCTION('DATE_FORMAT', o.orderDateTime, '%d-%m-%Y') " +
                        "ORDER BY FUNCTION('DATE_FORMAT', o.orderDateTime, '%d-%m-%Y')")
        List<Object[]> getRevenueByDay(
                        @Param("status") OrderStatus status,
                        @Param("startDate") LocalDateTime start,
                        @Param("endDate") LocalDateTime end);

        @Query("SELECT FUNCTION('DATE_FORMAT', o.orderDateTime, '%m-%Y'),SUM(o.subTotal) " +
                        "FROM Order o " +
                        "WHERE o.orderStatus = :status " +
                        "AND o.orderDateTime BETWEEN :startDate AND :endDate " +
                        "GROUP BY FUNCTION('DATE_FORMAT', o.orderDateTime, '%m-%Y') " +
                        "ORDER BY FUNCTION('DATE_FORMAT', o.orderDateTime, '%m-%Y')")
        List<Object[]> getRevenueByMonth(
                        @Param("status") OrderStatus status,
                        @Param("startDate") LocalDateTime start,
                        @Param("endDate") LocalDateTime end);

        @Query("SELECT FUNCTION('DATE_FORMAT', o.orderDateTime, '%Y'),SUM(o.subTotal) " +
                        "FROM Order o " +
                        "WHERE o.orderStatus = :status " +
                        "AND o.orderDateTime BETWEEN :startDate AND :endDate " +
                        "GROUP BY FUNCTION('DATE_FORMAT', o.orderDateTime, '%Y') " +
                        "ORDER BY FUNCTION('DATE_FORMAT', o.orderDateTime, '%Y')")
        List<Object[]> getRevenueByYear(
                        @Param("status") OrderStatus status,
                        @Param("startDate") LocalDateTime start,
                        @Param("endDate") LocalDateTime end);

        @Query(value = "SELECT " +
                        "SUM(COMPLETED) AS COMPLETED, " +
                        "SUM(SHIPPED) AS SHIPPED, " +
                        "SUM(PAID) AS PAID, " +
                        "SUM(CANCELLED) AS CANCELLED, " +
                        "SUM(FAILED) AS FAILED " +
                        "FROM ( " +
                        "    SELECT " +
                        "           CASE WHEN order_status = 'COMPLETED' THEN 1 ELSE 0 END AS COMPLETED, " +
                        "           CASE WHEN order_status = 'SHIPPED' THEN 1 ELSE 0 END AS SHIPPED, " +
                        "           CASE WHEN order_status = 'PAID' THEN 1 ELSE 0 END AS PAID, " +
                        "           CASE WHEN order_status = 'CANCELLED' THEN 1 ELSE 0 END AS CANCELLED, " +
                        "           CASE WHEN order_status = 'FAILED' THEN 1 ELSE 0 END AS FAILED " +
                        "    FROM orders " +
                        ") sub ", nativeQuery = true)
        List<Object[]> getOrderOverview();

        @Query(value = "SELECT date, " +
                        "SUM(COMPLETED) AS COMPLETED, " +
                        "SUM(SHIPPED) AS SHIPPED, " +
                        "SUM(PAID) AS PAID, " +
                        "SUM(CANCELLED) AS CANCELLED, " +
                        "SUM(FAILED) AS FAILED " +
                        "FROM ( " +
                        "    SELECT DATE_FORMAT(order_date_time, '%d-%m-%Y') AS date, " +
                        "           CASE WHEN order_status = 'COMPLETED' THEN 1 ELSE 0 END AS COMPLETED, " +
                        "           CASE WHEN order_status = 'SHIPPED' THEN 1 ELSE 0 END AS SHIPPED, " +
                        "           CASE WHEN order_status = 'PAID' THEN 1 ELSE 0 END AS PAID, " +
                        "           CASE WHEN order_status = 'CANCELLED' THEN 1 ELSE 0 END AS CANCELLED, " +
                        "           CASE WHEN order_status = 'FAILED' THEN 1 ELSE 0 END AS FAILED " +
                        "    FROM orders " +
                        "    WHERE order_date_time BETWEEN :startDate AND :endDate " +
                        ") sub " +
                        "GROUP BY date " +
                        "ORDER BY date", nativeQuery = true)
        List<Object[]> getOrderCountByDay(
                        @Param("startDate") LocalDateTime start,
                        @Param("endDate") LocalDateTime end);

}
