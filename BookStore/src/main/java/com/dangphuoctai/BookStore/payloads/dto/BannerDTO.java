package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerDTO {
    private Long bannerId;
    private String bannerName;
    private String image;
    private String link;
    private String position;
    private Boolean status;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
