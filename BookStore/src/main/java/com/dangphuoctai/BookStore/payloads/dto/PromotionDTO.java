package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;

import com.dangphuoctai.BookStore.enums.PromotionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    private Long promotionId;
    private String promotionName;
    private String promotionCode;
    private PromotionType promotionType;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    private Double value;
    private Double valueApply;
    private Boolean valueType;// true == % ,false == number

    private String description;
    private Boolean status;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
