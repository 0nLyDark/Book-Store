package com.dangphuoctai.BookStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.payloads.Statistic.ProductSale;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    boolean existsByProduct_ProductId(Long productId);

    List<OrderItem> findByProduct_ProductIdOrderByOrder_OrderDateTimeAsc(Long productId);

    @Query(value = """
                SELECT
                    p.product_id AS productId,
                    p.isbn AS isbn,
                    p.product_name AS productName,
                    (SELECT f.file_name FROM files f WHERE f.product_id = p.product_id LIMIT 1) AS image,
                    SUM(oi.quantity) AS totalQuantitySold
                FROM order_items oi
                JOIN products p ON oi.product_id = p.product_id
                GROUP BY p.product_id,p.isbn, p.product_name
                ORDER BY totalQuantitySold DESC
                LIMIT 25
            """, nativeQuery = true)
    List<Object[]> findBestSellingProducts();

    @Query("SELECT new com.dangphuoctai.BookStore.payloads.Statistic.ProductSale(" +
            "oi.product.productId, SUM(oi.quantity)) " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE o.orderStatus = 'COMPLETED' " +
            "GROUP BY oi.product.productId")
    List<ProductSale> findProductSalesQuantity();

}
