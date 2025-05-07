package com.dangphuoctai.BookStore.payloads.dto.Order;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private OrderDTO order;
    private List<ProductQuantity> productQuantities;
    private List<Long> productIds;
}
