package com.dangphuoctai.BookStore.service.impl;

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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dangphuoctai.BookStore.config.VNPayConfig;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.payloads.PaymentResDTO;
import com.dangphuoctai.BookStore.payloads.VnPayResponse;
import com.dangphuoctai.BookStore.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentResDTO createPaymentVNPAY(String vnp_IpAddr, String vnp_BankCode, String vnp_TxnRef, Long Amount) {
        String orderType = "other";
        long amount = Amount * 100;

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", vnp_BankCode);
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

        return paymentResDTO;
    }

    @Override
    public boolean checkTransaction(Long amount, String txnRef, String transactionDate) {
        String vnp_RequestId = VNPayConfig.getRandomNumber(8) + System.currentTimeMillis();
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String vnp_IpAddr = "127.0.0.1";
        Map<String, String> requestData = new HashMap<>();
        requestData.put("vnp_RequestId", vnp_RequestId);
        requestData.put("vnp_Version", VNPayConfig.vnp_Version);
        requestData.put("vnp_Command", "querydr");
        requestData.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        requestData.put("vnp_TxnRef", txnRef);
        requestData.put("vnp_OrderInfo", "Kiem tra giao dich don hang: " + txnRef);
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
                "Kiem tra giao dich don hang: " + txnRef);
        String hashKey = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, data);
        requestData.put("vnp_SecureHash", hashKey);

        // Gửi HTTP POST request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestData, headers);

        ResponseEntity<VnPayResponse> response = restTemplate.postForEntity(VNPayConfig.vnp_ApiUrl, entity,
                VnPayResponse.class);
        VnPayResponse vnPayResponse = response.getBody();
        log.info("vnpay    " + response);
        log.info("vnpaysss    " + vnPayResponse);
        if (!"00".equals(vnPayResponse.getTransactionStatus()) || !"00".equals(vnPayResponse.getResponseCode())) {
            throw new APIException("Giao dịch thanh toán chưa thành công");
        }
        if (vnPayResponse.getAmount() < amount * 100) {
            throw new APIException("Số tiền giao dịch không khớp");
        }

        return true;
    }

    @Override
    public boolean validateVNPaySignature(Map<String, String> fields) {
        log.info("aa  " + fields);
        String secureHashFromVNPay = fields.get("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String generatedHash = VNPayConfig.hashAllFields(fields);

        return generatedHash.equals(secureHashFromVNPay);
    }
}
