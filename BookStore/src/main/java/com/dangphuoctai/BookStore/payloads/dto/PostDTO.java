package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;

import com.dangphuoctai.BookStore.enums.PostType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long postId;
    private String title;
    private String slug;
    private String content;
    private PostType type;
    private Boolean status;
    private TopicDTO topic;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
