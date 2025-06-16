package com.dangphuoctai.BookStore.payloads.dto.Review;

import java.time.LocalDateTime;
import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.FileDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Long orderItemId;
    private String fullName;
    private String avatar;

    private List<FileDTO> images;

    private int star;
    private String comment;
    private LocalDateTime createdAt;

    private LocalDateTime updateAt;
}
