package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrderCount;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOverview;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticRevenue;
import com.dangphuoctai.BookStore.repository.OrderRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.repository.UserRepo;
import com.dangphuoctai.BookStore.service.StatisticService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public StatisticOverview getOverview() {
        long totalOrders = orderRepo.countByOrderStatus(OrderStatus.COMPLETED);
        double totalRevenue = orderRepo.getTotalRevenueByOrderStatus(OrderStatus.COMPLETED);
        long totalCustomers = userRepo.countByEnabled(true);
        long totalProducts = productRepo.countByStatus(true);
        StatisticOverview statisticOverview = new StatisticOverview();
        statisticOverview.setTotalOrders(totalOrders);
        statisticOverview.setTotalRevenue(totalRevenue);
        statisticOverview.setTotalCustomers(totalCustomers);
        statisticOverview.setTotalProducts(totalProducts);

        return statisticOverview;
    }

    @Override
    public List<StatisticRevenue> getRevenueByDate(LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Object[]> result = orderRepo.getRevenueByDay(OrderStatus.COMPLETED,
                startDateTime,
                endDateTime);

        List<StatisticRevenue> statisticRevenues = result.stream()
                .map(row -> {
                    log.info(java.util.Arrays.toString(row));
                    return new StatisticRevenue(
                            String.valueOf(row[0]),
                            (row[1] instanceof Double) ? (Double) row[1] : ((Number) row[1]).doubleValue());
                })
                .collect(Collectors.toList());

        return statisticRevenues;

    }

    @Override
    public List<StatisticOrderCount> getOrderCountByDay(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        List<Object[]> rawResults = orderRepo.getOrderCountByDay(startDateTime, endDateTime);

        List<StatisticOrderCount> statisticOrderCounts = rawResults.stream()
                .map(row -> new StatisticOrderCount(
                        String.valueOf(row[0]),
                        (row[1] instanceof Long) ? (Long) row[1] : ((Number) row[1]).longValue(),
                        (row[2] instanceof Long) ? (Long) row[2] : ((Number) row[2]).longValue(),
                        (row[3] instanceof Long) ? (Long) row[3] : ((Number) row[3]).longValue(),
                        (row[4] instanceof Long) ? (Long) row[4] : ((Number) row[4]).longValue(),
                        (row[5] instanceof Long) ? (Long) row[5] : ((Number) row[5]).longValue(),
                        (row[6] instanceof Long) ? (Long) row[6] : ((Number) row[6]).longValue()))
                .collect(Collectors.toList());

        return statisticOrderCounts;
    }

}
