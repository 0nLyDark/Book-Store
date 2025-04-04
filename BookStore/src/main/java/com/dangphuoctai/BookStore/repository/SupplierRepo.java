package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Supplier;

@Repository
public interface SupplierRepo extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findBySlug(String slug);

}
