package com.dangphuoctai.BookStore.payloads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String ward;
    private String buildingName;
    private String city;
    private String district;
    private String country;
    private String pincode;
}
