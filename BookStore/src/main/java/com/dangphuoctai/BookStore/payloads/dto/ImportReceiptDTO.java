package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportReceiptDTO {
    private Long importReceiptId;
    private SupplierDTO supplier;
    private Double totalAmount;
    private Long createdBy;
    private LocalDateTime importDate;
    private Boolean status;
    private String note;

    private List<ImportReceiptItemDTO> importReceiptItems;

}
