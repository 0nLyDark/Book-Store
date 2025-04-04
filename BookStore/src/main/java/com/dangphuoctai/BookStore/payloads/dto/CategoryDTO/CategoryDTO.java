package com.dangphuoctai.BookStore.payloads.dto.CategoryDTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long categoryId;

    private String categoryName;

    private String slug;

    private Boolean status;

    private ParentCategoryDTO parent;

    private List<ChildCategoryDTO> childrens;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
