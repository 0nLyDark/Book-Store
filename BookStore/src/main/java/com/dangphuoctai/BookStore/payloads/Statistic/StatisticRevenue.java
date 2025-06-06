package com.dangphuoctai.BookStore.payloads.Statistic;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticRevenue {

    private String date;

    private Double revenue;

}
