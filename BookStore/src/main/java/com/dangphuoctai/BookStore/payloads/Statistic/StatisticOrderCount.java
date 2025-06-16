package com.dangphuoctai.BookStore.payloads.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticOrderCount extends StatisticOrder {

    private String date;

    public StatisticOrderCount(
            String date,
            Long completedCount,
            Long shippedCount,
            Long paidCount,
            Long cancelledCount,
            Long failedCount) {
        super(completedCount, shippedCount, paidCount, cancelledCount, failedCount);
        this.date = date;
    }
}
