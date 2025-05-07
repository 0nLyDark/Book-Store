package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Cart;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.userId =:userId")
    Optional<Cart> findByUserId(Long userId);

}
