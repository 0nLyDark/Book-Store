package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId")
    Page<Order> findAllByUserId(Long userId, Pageable pageDetails);

    Optional<Order> findByEmail(String email);

}
