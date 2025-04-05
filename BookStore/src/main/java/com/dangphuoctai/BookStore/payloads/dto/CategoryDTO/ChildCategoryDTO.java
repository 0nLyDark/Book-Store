package com.dangphuoctai.BookStore.payloads.dto.CategoryDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildCategoryDTO extends CategoryDTO {

    private CategoryDTO parent;
}
