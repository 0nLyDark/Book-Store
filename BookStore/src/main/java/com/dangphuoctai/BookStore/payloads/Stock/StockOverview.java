package com.dangphuoctai.BookStore.payloads.Stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockOverview {
    private Long totalProductWarning;

    private Long totalProduct;

    private Long totalStock;

    private double totalCost;
}
