package com.dangphuoctai.BookStore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.payloads.Stock.ProductStock;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Optional<Product> findBySlug(String slug);

    Page<Product> findAll(Specification<Product> productSpecification, Pageable pageDetails);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // KHÓA GHI (Write lock)
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findByIdForUpdate(Long productId);

    @Query("SELECT c.id, COUNT(p) FROM Product p JOIN p.categories c WHERE c.id IN :categoryIds GROUP BY c.id")
    List<Object[]> countProductsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT c.id, COUNT(p) FROM Product p JOIN p.categories c WHERE c.id IN :categoryIds AND p.status = true GROUP BY c.id")
    List<Object[]> countActiveProductsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    long countByStatus(boolean b);

    @Query(value = """
                SELECT
                    p.product_id,
                    p.product_name,
                    COALESCE(SUM(oi.quantity), 0) AS total_quantity_sold,
                    COALESCE(SUM(oi.quantity * oi.price), 0) AS total_revenue,
                    COALESCE(SUM(iri.quantity), 0) AS total_quantity_imported,
                    COALESCE(SUM(iri.quantity * iri.price), 0) AS total_cost
                FROM product p
                LEFT JOIN order_item oi ON p.product_id = oi.product_id
                LEFT JOIN import_receipt_item iri ON p.product_id = iri.product_id
                GROUP BY p.product_id, p.product_name
            """, nativeQuery = true)
    List<Object[]> getProductProfitRaw();

    @Query("""
                SELECT new com.dangphuoctai.BookStore.payloads.Stock.ProductStock(p.productId, p.quantity)
                FROM Product p
                WHERE p.quantity > 0
            """)
    List<ProductStock> getProductStocks();

    @Query(value = """
                SELECT COUNT(*)
                FROM products
                WHERE quantity <= :qtyWarning
                  AND status = :status
            """, nativeQuery = true)
    Long countProductWarning(int qtyWarning, boolean status);
}
