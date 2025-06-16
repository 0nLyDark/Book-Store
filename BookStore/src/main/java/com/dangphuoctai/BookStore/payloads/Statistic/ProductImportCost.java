package com.dangphuoctai.BookStore.payloads.Statistic;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductImportCost {
    private Long productId;
    private Double cost;
    private int quantity;
    private String supplierName;
    private LocalDateTime importDate;

    public ProductImportCost(Long productId, Double cost, int quantity) {
        this.productId = productId;
        this.cost = cost;
        this.quantity = quantity;
    }

    public ProductImportCost(Long productId, Double cost, int quantity, String supplierName, LocalDateTime importDate) {
        this.productId = productId;
        this.cost = cost;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.importDate = importDate;
    }
}
