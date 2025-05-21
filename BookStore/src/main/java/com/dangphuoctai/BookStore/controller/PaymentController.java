package com.dangphuoctai.BookStore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dangphuoctai.BookStore.config.VNPayConfig;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.PaymentResDTO;
import com.dangphuoctai.BookStore.service.OrderService;
import com.dangphuoctai.BookStore.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    // @GetMapping("/public/payment/create_payment/{Amount}")
    public ResponseEntity<?> createPayment(HttpServletRequest req, @PathVariable Long Amount) {
        if (Amount == null || Amount == 0) {
            throw new ResourceNotFoundException("Payment", "amount", Amount);
        }

        // long amount = Integer.parseInt(req.getParameter("amount"))*100;
        // String bankCode = req.getParameter("bankCode");
        String orderType = "other";
        long amount = Amount * 100;
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        PaymentResDTO paymentResDTO = new PaymentResDTO();
        paymentResDTO.setStatus("Ok");
        paymentResDTO.setMessage("Successfully");
        paymentResDTO.setURL(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK).body(paymentResDTO);
    }

    @GetMapping("/public/payment/vnpay/paymentInfo")
    public ResponseEntity<?> transactionVNPAY(
            @RequestParam Map<String, String> allParams) {
        Boolean isVaild = paymentService.validateVNPaySignature(allParams);
        if (!isVaild) {
            throw new APIException("Thong tin khong hop le");
        }
        String TxnRef = allParams.get("vnp_TxnRef");
        String bankCode = allParams.get("vnp_BankCode");
        String responseCode = allParams.get("vnp_ResponseCode");
        String bankTranNo = allParams.get("vnp_BankTranNo");
        String payDate = allParams.get("vnp_PayDate");
        if (!"00".equals(responseCode)) {
            throw new APIException("Thanh toán không thành công, mã lỗi: " + responseCode);
        }
        boolean result = orderService.verifyVNPay(TxnRef, payDate, bankCode, bankTranNo);
        if (!result) {
            throw new APIException("Xác thực thanh toán thất bại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // @PostMapping("/public/payment/check-transaction")
    public ResponseEntity<?> checkTransaction(HttpServletRequest req, @RequestParam String txnRef,
            @RequestParam String transactionDate) {
        String vnp_RequestId = VNPayConfig.getRandomNumber(8) + System.currentTimeMillis();
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String vnp_IpAddr = "127.0.0.1";
        Map<String, String> requestData = new HashMap<>();
        requestData.put("vnp_RequestId", vnp_RequestId);
        requestData.put("vnp_Version", VNPayConfig.vnp_Version);
        requestData.put("vnp_Command", "querydr");
        requestData.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        requestData.put("vnp_TxnRef", txnRef);
        requestData.put("vnp_OrderInfo", "Kiem tra giao dich " + txnRef);
        requestData.put("vnp_TransactionDate", transactionDate);
        requestData.put("vnp_CreateDate", vnp_CreateDate);
        requestData.put("vnp_IpAddr", vnp_IpAddr);
        String data = String.join("|",
                vnp_RequestId,
                VNPayConfig.vnp_Version,
                "querydr",
                VNPayConfig.vnp_TmnCode,
                txnRef,
                transactionDate,
                vnp_CreateDate,
                vnp_IpAddr,
                "Kiem tra giao dich " + txnRef);
        String hashKey = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, data);
        log.info("data  " + data);
        log.info("key  " + hashKey);
        requestData.put("vnp_SecureHash", hashKey);

        // Gửi HTTP POST request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(VNPayConfig.vnp_ApiUrl, entity, String.class);

        return response;
    }

}
// localhost:8080/vnpay_jsp/?vnp_Amount=1000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP14582828&vnp_CardType=ATM&vnp_OrderInfo=Thanh+toan+don+hang%3A33407353&vnp_PayDate=20240915112656&vnp_ResponseCode=00&vnp_TmnCode=YPFL82KR&vnp_TransactionNo=14582828&vnp_TransactionStatus=00&vnp_TxnRef=33407353&vnp_SecureHash=24a14f5255de9ae1acf3711f1728f992be256cf0622f82646c95a610e5a59fff20c2cf2924315ef4c9c9089673ffe58edfd3f139fe52850f5128a82fe506e94a
