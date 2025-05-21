package com.dangphuoctai.BookStore.service;

import java.util.Map;

import com.dangphuoctai.BookStore.payloads.PaymentResDTO;

public interface PaymentService {
    PaymentResDTO createPaymentVNPAY(String vnp_IpAddr, String vnp_BankCode, String vnp_TxnRef, Long Amount);

    boolean checkTransaction(Long amount, String txnRef, String transactionDate);

    boolean validateVNPaySignature(Map<String, String> fields);
}
