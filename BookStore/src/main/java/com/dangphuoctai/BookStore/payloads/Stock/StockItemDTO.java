package com.dangphuoctai.BookStore.payloads.Stock;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockItemDTO {
    private double cost;
    private long quantity;
    private double totalCost;
    private String supplierName;
    private LocalDateTime importDate;

}
