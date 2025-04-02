package com.dangphuoctai.BookStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

}
