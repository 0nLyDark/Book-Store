package com.dangphuoctai.BookStore.payloads.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSale {

    private Long productId;

    private Long totalSold;

}
