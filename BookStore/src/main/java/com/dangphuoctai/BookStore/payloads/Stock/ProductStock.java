package com.dangphuoctai.BookStore.payloads.Stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStock {
    private Long productId;

    private int stockQuantity;

}
