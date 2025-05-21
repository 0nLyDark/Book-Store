package com.dangphuoctai.BookStore.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.entity.Order;
import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.repository.OrderItemRepo;
import com.dangphuoctai.BookStore.repository.OrderRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderScheduler {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Transactional
    // @Scheduled(fixedRate = 60 * 1000) // 1 phút 1 lần
    @Scheduled(cron = "0 0 1 * * *") // Mỗi ngày lúc 01:00
    public void expireUnpaidOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = now.minusHours(1);
        // LocalDateTime expiredTime = now.minusMinutes(1);

        List<Order> expiredOrders = orderRepo
                .findByOrderStatusAndOrderDateTimeBefore(OrderStatus.PENDING, expiredTime);

        for (Order order : expiredOrders) {
            for (OrderItem item : order.getOrderItems()) {
                Long productId = item.getProduct().getProductId();
                Product product = productRepo.findByIdForUpdate(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
                Integer newQuantity = product.getQuantity() + item.getQuantity();
                log.info("productId  " + productId);
                log.info("product  " + product.getQuantity());
                log.info("item  " + item.getQuantity());
                log.info("newQuantity  " + newQuantity);

                product.setQuantity(newQuantity);
                productRepo.save(product);
            }
        }
        orderRepo.deleteAll(expiredOrders);
    }
}
