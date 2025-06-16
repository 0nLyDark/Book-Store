package com.dangphuoctai.BookStore.payloads.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticOrder {
    private Long completedCount;
    private Long shippedCount;
    private Long paidCount;
    // private Long pendingCount;
    private Long cancelledCount;
    private Long failedCount;
}
