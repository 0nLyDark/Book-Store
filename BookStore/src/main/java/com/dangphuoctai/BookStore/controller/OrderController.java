package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderDTO;
import com.dangphuoctai.BookStore.payloads.dto.Order.OrderRequest;
import com.dangphuoctai.BookStore.payloads.dto.Order.ProductQuantity;
import com.dangphuoctai.BookStore.service.EmailService;
import com.dangphuoctai.BookStore.service.OrderService;
import com.dangphuoctai.BookStore.utils.Email;

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

    @PostMapping("/public/orders/customer")
    public ResponseEntity<OrderDTO> createCustomerOrder(@RequestBody OrderRequest orderRequest) {

        OrderDTO order = orderService.createCustomerOrder(orderRequest.getOrder(), orderRequest.getProductQuantities());

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }

    @PostMapping("/public/orders")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequest orderRequest) {

        OrderDTO order = orderService.createOrder(orderRequest.getOrder(), orderRequest.getProductIds());

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }

    @PostMapping("/staff/orders")
    public ResponseEntity<OrderDTO> createOrderOffline(@RequestBody OrderRequest orderRequest) {

        OrderDTO order = orderService.createOrderOffline(orderRequest.getOrder(), orderRequest.getProductQuantities());

        return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }

    @GetMapping("/public/orders/otp")
    public ResponseEntity<String> createOrderWithOTP(@RequestBody OtpDTO otpDTO) {

        String message = orderService.SendVerifyOrderEmail(otpDTO.getOrderId());

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
