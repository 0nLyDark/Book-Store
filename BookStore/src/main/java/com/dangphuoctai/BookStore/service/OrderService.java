package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderStatusDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.ProductQuantity;
import com.dangphuoctai.BookStore.payloads.response.OrderResponse;

public interface OrderService {

    OrderDTO getOrderById(Long orderId);

    OrderDTO getOrderByOrderCode(String orderCode);

    OrderResponse getAllOrderByUserId(Long userId, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    OrderResponse getAllOrder(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    Object createOrder(OrderDTO orderDTO, List<Long> productId, String vnp_IpAddr);

    Object createCustomerOrder(OrderDTO orderDTO, List<ProductQuantity> productQuantities, String vnp_IpAddr);

    Object createOrderOffline(OrderDTO orderDTO, List<ProductQuantity> productQuantities, String vnp_IpAddr);

    OrderDTO changeOrderStatus(OrderStatusDTO orderStatus);

    String SendVerifyOrderEmail(String orderCode);

    String generateOTPOrder(String orderCode);

    Boolean verityOTPEmail(OtpDTO otpDTO);

    Boolean verifyVNPay(String txnRef, String transactionDate, String bankCode, String BankTranNo);
}
