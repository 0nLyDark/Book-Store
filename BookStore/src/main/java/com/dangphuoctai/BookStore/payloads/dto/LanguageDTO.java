package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDTO {
    private Long languageId;
    private String name;
    private Boolean status;

    private Long createdBy;
    private Long updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
