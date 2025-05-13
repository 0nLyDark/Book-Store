package com.dangphuoctai.BookStore.payloads.dto;

import com.dangphuoctai.BookStore.enums.OTPType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpDTO {

    private String email;
    private String code;
    private String phoneNumber;
    private Long orderId;
    private OTPType type;
}
