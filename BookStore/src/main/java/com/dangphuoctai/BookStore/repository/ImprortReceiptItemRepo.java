package com.dangphuoctai.BookStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.ImportReceiptItem;
import com.dangphuoctai.BookStore.payloads.Statistic.ProductImportCost;

@Repository
public interface ImprortReceiptItemRepo extends JpaRepository<ImportReceiptItem, Long> {

        List<ImportReceiptItem> findByProduct_ProductIdOrderByImportReceipt_ImportDateAsc(Long productId);

        @Query("SELECT new com.dangphuoctai.BookStore.payloads.Statistic.ProductImportCost(" +
                        "iri.product.productId, iri.price, iri.quantity) " +
                        "FROM ImportReceiptItem iri " +
                        "JOIN iri.importReceipt ir " +
                        "WHERE ir.status = true " +
                        "ORDER BY ir.importDate ASC")
        List<ProductImportCost> findProductImportCosts();

        @Query("SELECT new com.dangphuoctai.BookStore.payloads.Statistic.ProductImportCost(" +
                        "iri.product.productId, iri.price, iri.quantity,ir.supplier.supplierName,ir.importDate) " +
                        "FROM ImportReceiptItem iri " +
                        "JOIN iri.importReceipt ir " +
                        "WHERE iri.product.productId IN :productIds AND ir.status = true " +
                        "ORDER BY ir.importDate DESC")
        List<ProductImportCost> findProductImportCosts(List<Long> productIds);
}
