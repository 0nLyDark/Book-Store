package com.dangphuoctai.BookStore.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.service.GHNService;

@Service
public class GHNServiceImpl implements GHNService {

        @Autowired
        private RestTemplate restTemplate;

        @Value("${ghn.base.url}")
        private String ghnBaseUrl;

        @Value("${ghn.token}")
        private String ghnToken;

        @Value("${ghn.shop_id}")
        private String shopId;

        @Override
        public Double calculateShippingFee(int service_type_id, Long toDistrictId, String toWardCode,
                        Long insuranceValue, List<OrderItem> orderItems) {
                String url = ghnBaseUrl + "/v2/shipping-order/fee";
                int sumWeight = orderItems.stream()
                                .mapToInt(item -> item.getProduct().getWeight() * item.getQuantity()).sum();
                List<Map<String, Object>> items = orderItems.stream()
                                .map(item -> {
                                        // Tạo một Map cho từng sản phẩm
                                        Map<String, Object> orderItem = new HashMap<>();
                                        orderItem.put("name", item.getProduct().getProductName());
                                        orderItem.put("quantity", item.getQuantity());
                                        orderItem.put("weight", item.getProduct().getWeight());
                                        return orderItem;
                                }).toList();
                Map<String, Object> body = new HashMap<>();
                body.put("service_type_id", service_type_id);
                body.put("insurance_value", insuranceValue);
                body.put("coupon", null);
                // body.put("from_district_id", 1450);
                body.put("to_district_id", toDistrictId);
                body.put("to_ward_code", toWardCode);
                body.put("weight", sumWeight);
                body.put("items", items);

                // body.put("length", 20);
                // body.put("width", 10);
                // body.put("height", 5);

                // Tạo headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Token", ghnToken);
                headers.set("ShopId", shopId);

                // Gửi request
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

                ResponseEntity<Map> response;
                try {
                        response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
                } catch (Exception e) {
                        throw new APIException("Error while calling GHN API: " + e.getMessage());
                }
                Map<String, Object> bodyResponse = response.getBody();
                Map<String, Object> data = (Map<String, Object>) bodyResponse.get("data");

                int total = (int) data.get("total");

                return (double) total;
        }
}
