package com.dangphuoctai.BookStore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.ImportReceipt;
import com.dangphuoctai.BookStore.entity.ImportReceiptItem;

@Repository
public interface ImportReceiptRepo extends JpaRepository<ImportReceipt, Long> {

    Page<ImportReceipt> findAll(Specification<ImportReceipt> specification, Pageable pageDetails);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM ImportReceipt i WHERE i.status = :status")
    double getTotalAmountByStatus(boolean status);

    // List<ImportReceiptItem>
    // findByProduct_ProductIdOrderByImportReceipt_ImportDateAsc(Long productId);

}
