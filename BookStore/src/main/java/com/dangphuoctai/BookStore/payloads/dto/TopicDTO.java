package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
    private Long topicId;
    private String topicName;
    private String slug;
    private String description;
    private Boolean status;
    private Long createdBy;
    private Long updateBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
