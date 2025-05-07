package com.dangphuoctai.BookStore.payloads.dto.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantity {
    private Long productId;
    private int quantity;
}
