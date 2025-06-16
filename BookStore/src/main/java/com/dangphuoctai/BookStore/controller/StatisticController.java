package com.dangphuoctai.BookStore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.payloads.Statistic.BestSellingProductDTO;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrder;
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

    @GetMapping("/staff/statistics/overview")
    public ResponseEntity<StatisticOverview> getOverview() {
        StatisticOverview overviewReponse = statisticService.getOverview();

        return new ResponseEntity<StatisticOverview>(overviewReponse, HttpStatus.OK);
    }

    @GetMapping("/staff/statistics/overview/order")
    public ResponseEntity<StatisticOrder> getOrderOverview() {
        StatisticOrder statisticOrder = statisticService.getOrderOverview();

        return new ResponseEntity<StatisticOrder>(statisticOrder, HttpStatus.OK);
    }

    @GetMapping("/staff/statistics/revenue/date")
    public ResponseEntity<List<StatisticRevenue>> getOverviewByDate(
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {
        List<StatisticRevenue> statisticRevenues = statisticService.getRevenueByDate(startDate, endDate);

        return new ResponseEntity<List<StatisticRevenue>>(statisticRevenues, HttpStatus.OK);
    }

    @GetMapping("/staff/statistics/orderCount/date")
    public ResponseEntity<List<StatisticOrderCount>> getOrderCountByDay(
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate) {

        List<StatisticOrderCount> orderCounts = statisticService.getOrderCountByDay(startDate, endDate);

        return new ResponseEntity<List<StatisticOrderCount>>(orderCounts, HttpStatus.OK);
    }

    @GetMapping("/staff/statistics/best-selling-products")
    public ResponseEntity<List<BestSellingProductDTO>> getTopSellingProducts() {

        List<BestSellingProductDTO> topProducts = statisticService.getBestSellingProduct();

        return new ResponseEntity<List<BestSellingProductDTO>>(topProducts, HttpStatus.OK);
    }
}
