package com.dangphuoctai.BookStore.payloads.response;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.ImportReceiptDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportReceiptResponse {
    private List<ImportReceiptDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
