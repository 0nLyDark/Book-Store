package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.entity.Order;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.ProductQuantity;
import com.dangphuoctai.BookStore.payloads.response.OrderResponse;

public interface OrderService {

    OrderDTO getOrderById(Long orderId);

    OrderResponse getAllOrderByUserId(Long userId, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    OrderResponse getAllOrder(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    OrderDTO createOrder(OrderDTO orderDTO, List<Long> productId);

    OrderDTO createCustomerOrder(OrderDTO orderDTO, List<ProductQuantity> productQuantities);

    OrderDTO createOrderOffline(OrderDTO orderDTO, List<ProductQuantity> productQuantities);

    String SendVerifyOrderEmail(Long orderId);

    String generateOTPOrder(Order order);

    Boolean verityOTPEmail(OtpDTO otpDTO);

}
