package com.dangphuoctai.BookStore.payloads.Stock;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.FileDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private Long productId;
    private String productName;
    private String slug;
    private String isbn;
    private int quantity;
    private double price;
    private double totalCost;
    private int discount;
    private Boolean status;
    private List<FileDTO> images;
    private List<StockItemDTO> stockItems;
}
