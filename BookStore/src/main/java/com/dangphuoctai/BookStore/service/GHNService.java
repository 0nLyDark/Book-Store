package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.entity.OrderItem;

public interface GHNService {
    Double calculateShippingFee(int service_type_id, Long toDistrictId, String toWardCode, Long insuranceValue,
            List<OrderItem> orderItems);

}
