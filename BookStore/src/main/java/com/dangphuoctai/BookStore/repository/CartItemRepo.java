package com.dangphuoctai.BookStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.CartItem;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

}
