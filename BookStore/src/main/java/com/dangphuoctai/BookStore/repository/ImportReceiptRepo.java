package com.dangphuoctai.BookStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.ImportReceipt;

@Repository
public interface ImportReceiptRepo extends JpaRepository<ImportReceipt, Long> {

}
