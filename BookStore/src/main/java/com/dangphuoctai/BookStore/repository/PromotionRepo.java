package com.dangphuoctai.BookStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Promotion;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long> {

}
