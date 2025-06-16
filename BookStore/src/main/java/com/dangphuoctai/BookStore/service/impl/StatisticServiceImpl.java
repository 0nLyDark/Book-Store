package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.g;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.payloads.Statistic.BestSellingProductDTO;
import com.dangphuoctai.BookStore.payloads.Statistic.ProductImportCost;
import com.dangphuoctai.BookStore.payloads.Statistic.ProductSale;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrder;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOrderCount;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticOverview;
import com.dangphuoctai.BookStore.payloads.Statistic.StatisticRevenue;
import com.dangphuoctai.BookStore.repository.ImportReceiptRepo;
import com.dangphuoctai.BookStore.repository.ImprortReceiptItemRepo;
import com.dangphuoctai.BookStore.repository.OrderItemRepo;
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
        private OrderItemRepo orderItemRepo;

        @Autowired
        private ImportReceiptRepo importReceiptRepo;

        @Autowired
        private ImprortReceiptItemRepo importReceiptItemRepo;

        @Autowired
        private UserRepo userRepo;

        @Override
        public StatisticOverview getOverview() {
                long totalOrders = orderRepo.countByOrderStatus(OrderStatus.COMPLETED);
                double totalRevenue = orderRepo.getTotalRevenueByOrderStatus(OrderStatus.COMPLETED);
                double totalCostSale = getTotalCostSale();
                long totalCustomers = userRepo.countByEnabled(true);
                long totalProducts = productRepo.countByStatus(true);
                StatisticOverview statisticOverview = new StatisticOverview();
                statisticOverview.setTotalOrders(totalOrders);
                statisticOverview.setTotalRevenue(totalRevenue);
                statisticOverview.setTotalProfit(totalRevenue - totalCostSale);
                statisticOverview.setTotalCustomers(totalCustomers);
                statisticOverview.setTotalProducts(totalProducts);

                return statisticOverview;
        }

        private double getTotalCostSale() {
                List<ProductSale> productSales = orderItemRepo.findProductSalesQuantity();
                List<ProductImportCost> productImportCosts = importReceiptItemRepo.findProductImportCosts();
                Map<Long, LinkedList<ProductImportCost>> importCostMap = new HashMap<Long, LinkedList<ProductImportCost>>();
                for (ProductImportCost importCost : productImportCosts) {
                        if (!importCostMap.containsKey(importCost.getProductId())) {
                                importCostMap.put(importCost.getProductId(), new LinkedList<>());
                        }
                        importCostMap.get(importCost.getProductId()).add(importCost);
                }
                // Tính tổng cost sản phẩm đã bán
                double totalCost = 0;

                for (ProductSale sale : productSales) {
                        Long productId = sale.getProductId();
                        Long quantityToSell = sale.getTotalSold();

                        LinkedList<ProductImportCost> importQueue = importCostMap.get(productId);
                        if (importQueue == null || importQueue.isEmpty())
                                continue;

                        while (quantityToSell > 0 && !importQueue.isEmpty()) {
                                ProductImportCost importBatch = importQueue.peek();

                                long availableQty = importBatch.getQuantity();
                                double cost = importBatch.getCost();

                                if (availableQty <= quantityToSell) {
                                        // Dùng hết đợt nhập này
                                        totalCost += availableQty * cost;
                                        quantityToSell -= availableQty;
                                        importQueue.poll(); // remove khỏi queue
                                } else {
                                        // Chỉ dùng một phần của đợt nhập này
                                        totalCost += quantityToSell * cost;
                                        importBatch.setQuantity((int) (availableQty - quantityToSell));
                                        quantityToSell = 0L;
                                }
                        }
                }
                return totalCost;
        }

        @Override
        public StatisticOrder getOrderOverview() {
                Object[] result = orderRepo.getOrderOverview().get(0);

                StatisticOrder statisticOrder = new StatisticOrder(
                                ((Number) result[0]).longValue(), // completedCount
                                ((Number) result[1]).longValue(), // shippedCount
                                ((Number) result[2]).longValue(), // paidCount
                                ((Number) result[3]).longValue(), // cancelledCount
                                ((Number) result[4]).longValue() // failedCount
                );

                return statisticOrder;
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
                                                        (row[1] instanceof Double) ? (Double) row[1]
                                                                        : ((Number) row[1]).doubleValue());
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
                                                (row[1] instanceof Long) ? (Long) row[1]
                                                                : ((Number) row[1]).longValue(),
                                                (row[2] instanceof Long) ? (Long) row[2]
                                                                : ((Number) row[2]).longValue(),
                                                (row[3] instanceof Long) ? (Long) row[3]
                                                                : ((Number) row[3]).longValue(),
                                                (row[4] instanceof Long) ? (Long) row[4]
                                                                : ((Number) row[4]).longValue(),
                                                (row[5] instanceof Long) ? (Long) row[5]
                                                                : ((Number) row[5]).longValue()))
                                .collect(Collectors.toList());

                return statisticOrderCounts;
        }

        @Override
        public List<BestSellingProductDTO> getBestSellingProduct() {

                List<BestSellingProductDTO> result = orderItemRepo.findBestSellingProducts()
                                .stream()
                                .map(row -> new BestSellingProductDTO(
                                                ((Number) row[0]).longValue(),
                                                (String) row[1],
                                                (String) row[2],
                                                (String) row[3],
                                                ((Number) row[4]).longValue()))
                                .collect(Collectors.toList());

                return result;
        }

}
