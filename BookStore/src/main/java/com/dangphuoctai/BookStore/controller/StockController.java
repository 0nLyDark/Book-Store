package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.Stock.StockOverview;
import com.dangphuoctai.BookStore.payloads.response.ProductResponse;
import com.dangphuoctai.BookStore.payloads.response.StockResponse;
import com.dangphuoctai.BookStore.service.StockService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/staff/stocks/qtyWarning")
    public ResponseEntity<Integer> getQuantityWarning() {
        int qtyWarning = stockService.getQuantityWarning();

        return new ResponseEntity<Integer>(qtyWarning, HttpStatus.OK);
    }

    @PutMapping("/staff/stocks/qtyWarning/{qty}")
    public ResponseEntity<Integer> updateQuantityWarning(@PathVariable Integer qty) {
        int qtyWarning = stockService.setQuantityWarning(qty);

        return new ResponseEntity<Integer>(qtyWarning, HttpStatus.OK);
    }

    @GetMapping("/staff/stocks/overview")
    public ResponseEntity<StockOverview> getStockOverview() {
        StockOverview stockOverview = stockService.getStockOverview();

        return new ResponseEntity<StockOverview>(stockOverview, HttpStatus.OK);
    }

    @GetMapping("/staff/stocks")
    public ResponseEntity<StockResponse> getAllStock(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "isWarning", defaultValue = "false", required = false) Boolean isWarning,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        StockResponse stockResponse = stockService.getStock(
                keyword, isbn, status, isWarning,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "productId" : sortBy,
                sortOrder);

        return new ResponseEntity<StockResponse>(stockResponse, HttpStatus.OK);
    }

}
