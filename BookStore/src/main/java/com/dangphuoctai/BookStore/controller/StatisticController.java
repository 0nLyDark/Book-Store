package com.dangphuoctai.BookStore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrderCount;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOverview;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticRevenue;
import com.dangphuoctai.BookStore.service.StatisticService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/statff/statistics/overview")
    public StatisticOverview getOverview() {
        StatisticOverview overviewReponse = statisticService.getOverview();

        return overviewReponse;
    }

    @GetMapping("/statff/statistics/revenue/date")
    public List<StatisticRevenue> getOverviewByDate(
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {
        List<StatisticRevenue> statisticRevenues = statisticService.getRevenueByDate(startDate, endDate);

        return statisticRevenues;
    }

    @GetMapping("/statff/statistics/orderCount/date")
    public List<StatisticOrderCount> getOrderCountByDay(
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {

        List<StatisticOrderCount> orderCounts = statisticService.getOrderCountByDay(startDate, endDate);

        return orderCounts;
    }

}
