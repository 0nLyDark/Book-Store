package com.dangphuoctai.BookStore.payloads.dto.Order;

import com.dangphuoctai.BookStore.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusDTO {

    private Long orderId;

    private String orderCode;

    private OrderStatus orderStatus;

}
