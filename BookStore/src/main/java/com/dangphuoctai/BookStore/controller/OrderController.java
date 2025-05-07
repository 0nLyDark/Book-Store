package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderRequest;
import com.dangphuoctai.BookStore.payloads.dto.Order.ProductQuantity;
import com.dangphuoctai.BookStore.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/public/orders/customer")
    public ResponseEntity<OrderDTO> createCustomerOrder(@RequestBody OrderRequest orderRequest) {

        OrderDTO order = orderService.createCustomerOrder(orderRequest.getOrder(), orderRequest.getProductQuantities());

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }

    @PostMapping("/public/orders")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequest orderRequest) {

        OrderDTO order = orderService.createOrder(orderRequest.getOrder(), orderRequest.getProductIds());

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }

    @PostMapping("/staff/orders")
    public ResponseEntity<OrderDTO> createOrderOffline(@RequestBody OrderRequest orderRequest) {

        OrderDTO order = orderService.createOrderOffline(orderRequest.getOrder(), orderRequest.getProductQuantities());

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }
}
