package com.dangphuoctai.BookStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.ImportReceiptDTO;
import com.dangphuoctai.BookStore.payloads.response.ImportReceiptResponse;
import com.dangphuoctai.BookStore.service.ImportReceiptService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImportReceiptController {

    @Autowired
    private ImportReceiptService importReceiptService;

    @GetMapping("/staff/import-receipts/{importReceiptId}")
    public ResponseEntity<ImportReceiptDTO> getImportReceiptById(@PathVariable Long importReceiptId) {

        ImportReceiptDTO importReceiptDTO = importReceiptService.getImportReceiptById(importReceiptId);

        return new ResponseEntity<ImportReceiptDTO>(importReceiptDTO, HttpStatus.OK);
    }

    @GetMapping("/staff/import-receipts")
    public ResponseEntity<ImportReceiptResponse> getAllLanguages(
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "supplierId", required = false) Long supplierId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_IMPORTRECEIPT_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        ImportReceiptResponse importReceiptResponse = importReceiptService.getAllImportReceipts(status, userId,
                supplierId,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "importReceiptId" : sortBy,
                sortOrder);

        return new ResponseEntity<ImportReceiptResponse>(importReceiptResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/import-receipts")
    public ResponseEntity<ImportReceiptDTO> postMethodName(@RequestBody ImportReceiptDTO importReceipt) {

        ImportReceiptDTO importReceiptDTO = importReceiptService.createImportReceipt(importReceipt);

        return new ResponseEntity<ImportReceiptDTO>(importReceiptDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/import-receipts")
    public ResponseEntity<ImportReceiptDTO> updateImportReceiptStatus(@RequestBody ImportReceiptDTO importReceipt) {

        ImportReceiptDTO importReceiptDTO = importReceiptService.updateImportReceiptStatus(
                importReceipt.getImportReceiptId(),
                importReceipt.getStatus());

        return new ResponseEntity<ImportReceiptDTO>(importReceiptDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/import-receipts/{importReceiptId}")
    public ResponseEntity<String> deleteImportReceipt(@PathVariable Long importReceiptId) {

        String message = importReceiptService.deleteImportReceipt(importReceiptId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

}
