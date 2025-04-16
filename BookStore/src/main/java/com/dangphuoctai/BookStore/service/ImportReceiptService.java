package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.dto.ImportReceiptDTO;
import com.dangphuoctai.BookStore.payloads.response.ImportReceiptResponse;

public interface ImportReceiptService {

    ImportReceiptDTO createImportReceipt(ImportReceiptDTO importReceiptDTO);

    ImportReceiptDTO getImportReceiptById(Long importReceiptId);

    ImportReceiptResponse getAllImportReceipts(Boolean status,Long userId,Long supplierId, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrderBy);

    ImportReceiptDTO updateImportReceiptStatus(Long importReceiptId, Boolean status);

    String deleteImportReceipt(Long importReceiptId);

}