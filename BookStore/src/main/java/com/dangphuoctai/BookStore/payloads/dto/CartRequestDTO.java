package com.dangphuoctai.BookStore.payloads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartRequestDTO {
    private Long cartId;
    private Long productId;
    private Integer quantity;
}
