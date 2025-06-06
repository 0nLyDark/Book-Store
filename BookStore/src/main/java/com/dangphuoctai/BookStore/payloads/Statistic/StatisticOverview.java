package com.dangphuoctai.BookStore.payloads.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticOverview {
    private double totalRevenue;
    private double totalProfit;
    private long totalOrders;
    private long totalCustomers;
    private long totalProducts;
}
