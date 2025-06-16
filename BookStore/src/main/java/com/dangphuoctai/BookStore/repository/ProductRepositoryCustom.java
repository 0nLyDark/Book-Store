package com.dangphuoctai.BookStore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.Product;

public interface ProductRepositoryCustom {
    Page<Product> fullTextSearchWithFilters(String keyword, Specification<Product> spec, Pageable pageable);
}
