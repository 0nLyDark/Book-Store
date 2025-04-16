package com.dangphuoctai.BookStore.payloads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportReceiptItemDTO {

    private Long importReceiptItemId;
    private ProductDTO product;
    private int quantity;
    private Double price;
    private Integer discount;
    private Double totalPrice;

}
