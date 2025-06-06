package com.dangphuoctai.BookStore.payloads.dto.PostDTO;

import java.time.LocalDateTime;

import com.dangphuoctai.BookStore.enums.PostType;
import com.dangphuoctai.BookStore.payloads.dto.TopicDTO;

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
    private String image;

    private String content;
    private PostType type;
    private Boolean status;
    private TopicDTO topic;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
