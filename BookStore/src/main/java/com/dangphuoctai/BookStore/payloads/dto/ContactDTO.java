package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO {
    private Long contactId;
    private String email;
    private String mobileNumber;
    private String title;
    private String content;

    private Boolean isRely;
    private Boolean isRead;

    private LocalDateTime createdAt;
}
