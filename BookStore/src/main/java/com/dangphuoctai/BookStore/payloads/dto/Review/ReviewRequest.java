package com.dangphuoctai.BookStore.payloads.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewRequest extends ReviewDTO {
    private Long orderItemId;
}
