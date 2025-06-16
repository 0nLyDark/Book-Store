package com.dangphuoctai.BookStore.service;

import java.time.LocalDate;
import java.util.List;

import com.dangphuoctai.BookStore.payloads.Statistic.BestSellingProductDTO;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrder;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrderCount;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOverview;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticRevenue;

public interface StatisticService {

    StatisticOverview getOverview();

    StatisticOrder getOrderOverview();

    List<StatisticRevenue> getRevenueByDate(LocalDate starDate, LocalDate endDate);

    List<StatisticOrderCount> getOrderCountByDay(LocalDate start, LocalDate end);

    List<BestSellingProductDTO> getBestSellingProduct();
}
