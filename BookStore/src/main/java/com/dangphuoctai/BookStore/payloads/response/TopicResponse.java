package com.dangphuoctai.BookStore.payloads.response;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.TopicDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponse {
    private List<TopicDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
