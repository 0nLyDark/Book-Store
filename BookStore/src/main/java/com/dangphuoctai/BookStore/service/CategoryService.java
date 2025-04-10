package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.CategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.ChildCategoryDTO;
import com.dangphuoctai.BookStore.payloads.response.CategoryResponse;

public interface CategoryService {
    CategoryDTO getCategoryById(Long categoryId);

    List<CategoryDTO> getManyCategoryById(List<Long> categoryIds);

    CategoryDTO getCategoryBySlug(String slug);

    CategoryResponse getAllCategories(String type, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    CategoryDTO createCategory(ChildCategoryDTO categoryDTO);

    CategoryDTO updateCategory(ChildCategoryDTO categoryDTO);

    String deleteCategory(Long categoryId);

}
