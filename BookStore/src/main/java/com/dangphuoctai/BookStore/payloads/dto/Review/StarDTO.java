package com.dangphuoctai.BookStore.payloads.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StarDTO {
    private Double averageStar;
    private Long totalReviews;
}
