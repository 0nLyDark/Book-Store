package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dangphuoctai.BookStore.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;

    private String email;

    private String deliveryName;

    private String deliveryPhone;

    private List<OrderItemDTO> orderItems = new ArrayList<>();

    private AddressDTO address;

    private LocalDateTime orderDateTime;

    private Payment payment;

    private double totalAmount;

    private String orderStatus;
}