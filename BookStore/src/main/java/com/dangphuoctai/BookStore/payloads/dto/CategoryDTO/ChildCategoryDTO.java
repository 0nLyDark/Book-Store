package com.dangphuoctai.BookStore.payloads.dto.CategoryDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildCategoryDTO extends CategoryDTO {

    private CategoryDTO parent;
}
