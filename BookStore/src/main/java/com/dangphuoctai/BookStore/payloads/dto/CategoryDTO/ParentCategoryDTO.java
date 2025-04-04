package com.dangphuoctai.BookStore.payloads.dto.CategoryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentCategoryDTO {
    private Long categoryId;
    private String categoryName;
    private String slug;
    private Boolean status;
}
