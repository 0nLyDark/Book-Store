package com.dangphuoctai.BookStore.payloads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    // private Long cartItemId;

    private ProductDTO product;

    private int quantity;
}
