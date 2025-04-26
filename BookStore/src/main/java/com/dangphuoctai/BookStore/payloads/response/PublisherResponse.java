package com.dangphuoctai.BookStore.payloads.response;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.PublisherDTO.PublisherDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherResponse {
    private List<PublisherDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
