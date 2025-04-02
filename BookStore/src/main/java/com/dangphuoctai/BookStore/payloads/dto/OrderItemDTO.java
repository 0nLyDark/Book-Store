package com.dangphuoctai.BookStore.payloads.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private Long orderItemId;

    private ProductDTO product;

    private int quantity;

    private int discount;

    private double price;
}
