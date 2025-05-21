package com.dangphuoctai.BookStore.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Order;
import com.dangphuoctai.BookStore.enums.OrderStatus;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId")
    Page<Order> findAllByUserId(Long userId, Pageable pageDetails);

    Optional<Order> findByEmail(String email);

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByOrderStatusAndOrderDateTimeBefore(OrderStatus pending, LocalDateTime expiredTime);

}
