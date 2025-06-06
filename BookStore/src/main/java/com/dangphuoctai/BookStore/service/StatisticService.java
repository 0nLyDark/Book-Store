package com.dangphuoctai.BookStore.service;

import java.time.LocalDate;
import java.util.List;

import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrderCount;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOverview;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticRevenue;

public interface StatisticService {

    StatisticOverview getOverview();

    List<StatisticRevenue> getRevenueByDate(LocalDate starDate, LocalDate endDate);

    List<StatisticOrderCount> getOrderCountByDay(LocalDate start, LocalDate end);
}
