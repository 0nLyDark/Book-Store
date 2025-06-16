package com.dangphuoctai.BookStore.payloads.Statistic;

public record ProductProfitDTO(
        Long productId,
        String productName,
        int totalSold,
        double totalRevenue,
        double totalCost,
        double profit) {
}