package com.dangphuoctai.BookStore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.CategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.ChildCategoryDTO;
import com.dangphuoctai.BookStore.payloads.response.CategoryResponse;
import com.dangphuoctai.BookStore.service.CategoryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);

        return new ResponseEntity<CategoryDTO>(categoryDTO, HttpStatus.OK);
    }

    @GetMapping("/public/categories/ids")
    public ResponseEntity<List<CategoryDTO>> getCategoryBySlug(@RequestParam(value = "id") List<Long> categoryIds) {
        List<CategoryDTO> categoryDTOs = categoryService.getManyCategoryById(categoryIds);

        return new ResponseEntity<List<CategoryDTO>>(categoryDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/categories/slug/{slug}")
    public ResponseEntity<CategoryDTO> getCategoryBySlug(@PathVariable String slug) {
        CategoryDTO categoryDTO = categoryService.getCategoryBySlug(slug);

        return new ResponseEntity<CategoryDTO>(categoryDTO, HttpStatus.OK);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "type", defaultValue = "parent", required = false) String type,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        CategoryResponse categoryResponse = categoryService.getAllCategories(type,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "categoryId" : sortBy,
                sortOrder);

        return new ResponseEntity<CategoryResponse>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody ChildCategoryDTO category) {
        CategoryDTO categoryDTO = categoryService.createCategory(category);

        return new ResponseEntity<CategoryDTO>(categoryDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/categories")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody ChildCategoryDTO category) {
        System.out.println(category.getCategoryId());
        System.out.println(category.getCategoryName());

        CategoryDTO categoryDTO = categoryService.updateCategory(category);

        return new ResponseEntity<CategoryDTO>(categoryDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        String result = categoryService.deleteCategory(categoryId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

}
