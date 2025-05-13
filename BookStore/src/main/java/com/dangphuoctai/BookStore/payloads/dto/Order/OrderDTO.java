package com.dangphuoctai.BookStore.payloads.dto.Order;

import java.time.LocalDateTime;
import java.util.List;

import com.dangphuoctai.BookStore.entity.Payment;

import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.enums.OrderType;
import com.dangphuoctai.BookStore.payloads.dto.AddressDTO;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;

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

    private List<OrderItemDTO> orderItems;

    private AddressDTO address;

    private LocalDateTime orderDateTime;

    private PaymentDTO payment;

    private PromotionDTO coupon;

    private PromotionDTO freeship;

    private double subTotal;

    private double priceShip;

    private double totalAmount;

    private OrderStatus orderStatus;

    private OrderType orderType;

}