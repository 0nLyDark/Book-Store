package com.dangphuoctai.BookStore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.CategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.ChildCategoryDTO;
import com.dangphuoctai.BookStore.payloads.response.CategoryResponse;

public interface CategoryService {
    CategoryDTO getCategoryById(Long categoryId);

    List<CategoryDTO> getManyCategoryById(List<Long> categoryIds);

    CategoryDTO getCategoryBySlug(String slug);

    CategoryResponse getAllCategories(Boolean status, String type, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    CategoryDTO createCategory(ChildCategoryDTO categoryDTO, MultipartFile image) throws IOException;

    CategoryDTO updateCategory(ChildCategoryDTO categoryDTO, MultipartFile image) throws IOException;

    String deleteCategory(Long categoryId);

}
