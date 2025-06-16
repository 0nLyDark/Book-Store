package com.dangphuoctai.BookStore.payloads.Statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestSellingProductDTO {
    private Long productId;
    private String isbn;
    private String productName;
    private String image;
    private Long totalQuantitySold;
}