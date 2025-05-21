package com.dangphuoctai.BookStore.payloads.dto.Order;

import com.dangphuoctai.BookStore.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentId;

    private PaymentMethod paymentMethod;

    private String bankCode;

    private String paymentCode;
}
