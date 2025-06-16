package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.Stock.StockOverview;
import com.dangphuoctai.BookStore.payloads.response.StockResponse;

public interface StockService {

    int setQuantityWarning(int quantity);

    int getQuantityWarning();

    StockResponse getStock(String keyword, String isbn, Boolean status, Boolean isWarning, Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder);

    StockOverview getStockOverview();

}
