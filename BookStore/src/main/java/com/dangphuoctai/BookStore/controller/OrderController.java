package com.dangphuoctai.BookStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.config.VNPayConfig;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderRequest;
import com.dangphuoctai.BookStore.payloads.response.OrderResponse;
import com.dangphuoctai.BookStore.service.EmailService;
import com.dangphuoctai.BookStore.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/public/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {

        OrderDTO order = orderService.getOrderById(orderId);

        return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
    }

    @GetMapping("/public/orders/user/{userId}")
    public ResponseEntity<OrderResponse> getOrdersByUserId(@PathVariable Long userId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        OrderResponse orderResponse = orderService.getAllOrderByUserId(userId,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "orderId" : sortBy,
                sortOrder);

        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @GetMapping("/staff/orders")
    public ResponseEntity<OrderResponse> getAllOrder(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        OrderResponse orderResponse = orderService.getAllOrder(
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "orderId" : sortBy,
                sortOrder);

        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @PostMapping("/public/orders/customer")
    public ResponseEntity<?> createCustomerOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest req) {
        String ip = VNPayConfig.getIpAddress(req);
        Object order = orderService.createCustomerOrder(orderRequest.getOrder(), orderRequest.getProductQuantities(),
                ip);

        return new ResponseEntity<Object>(order, HttpStatus.CREATED);
    }

    @PostMapping("/public/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest req) {
        String ip = VNPayConfig.getIpAddress(req);
        Object order = orderService.createOrder(orderRequest.getOrder(), orderRequest.getProductIds(), ip);

        return new ResponseEntity<Object>(order, HttpStatus.CREATED);
    }

    @PostMapping("/staff/orders")
    public ResponseEntity<OrderDTO> createOrderOffline(@RequestBody OrderRequest orderRequest, HttpServletRequest req) {
        String ip = VNPayConfig.getIpAddress(req);
        OrderDTO order = orderService.createOrderOffline(orderRequest.getOrder(), orderRequest.getProductQuantities(),
                ip);

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }

    @GetMapping("/public/orders/code/{orderCode}/otp")
    public ResponseEntity<String> createOrderWithOTP(@PathVariable String orderCode) {

        String message = orderService.SendVerifyOrderEmail(orderCode);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

    @PostMapping("/public/orders/otp")
    public ResponseEntity<String> verifyOrderOTP(@RequestBody OtpDTO otpDTO) {

        Boolean result = orderService.verityOTPEmail(otpDTO);
        if (result == false) {
            throw new APIException("Mã OTP không hợp lệ");
        }

        return new ResponseEntity<String>("Xác thực đơn hàng thành công", HttpStatus.OK);
    }
}
