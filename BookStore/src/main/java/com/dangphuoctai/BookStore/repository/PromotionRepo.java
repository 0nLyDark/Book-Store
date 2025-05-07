package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Promotion;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long> {

    Page<Promotion> findAll(Specification<Promotion> promotionSpecification, Pageable pageDetails);

    Optional<Promotion> findByPromotionCode(String promotionCode);

}
