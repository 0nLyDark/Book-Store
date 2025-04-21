package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.entity.Category;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.CategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.ChildCategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.ParentCategoryDTO;
import com.dangphuoctai.BookStore.payloads.response.CategoryResponse;
import com.dangphuoctai.BookStore.repository.CategoryRepo;
import com.dangphuoctai.BookStore.service.CategoryService;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.utils.CreateSlug;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        return modelMapper.map(category, ChildCategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> getManyCategoryById(List<Long> categoryIds) {
        List<Category> categories = categoryRepo.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new ResourceNotFoundException("Category", "categoryIds", categoryIds);
        }
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, ChildCategoryDTO.class))
                .collect(Collectors.toList());

        return categoryDTOs;
    }

    @Override
    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));

        return modelMapper.map(category, ChildCategoryDTO.class);
    }

    @Override
    public CategoryResponse getAllCategories(Boolean status, String type, Integer pageNumber, Integer pageSize,
            String sortBy,
            String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Category> pageCategories;
        List<CategoryDTO> categoryDTOs;

        if (type.equalsIgnoreCase("parent")) {
            if (status == null) {
                pageCategories = categoryRepo.findAllByParentIsNull(pageDetails);
            } else {
                pageCategories = categoryRepo.findAllByParentIsNullAndStatus(status, pageDetails);
            }
            categoryDTOs = pageCategories.getContent().stream()
                    .map(category -> modelMapper.map(category, ParentCategoryDTO.class)).collect(Collectors.toList());
        } else {
            if (status == null) {
                pageCategories = categoryRepo.findAll(pageDetails);
            } else {
                pageCategories = categoryRepo.findAllByStatus(status, pageDetails);
            }
            categoryDTOs = pageCategories.getContent().stream()
                    .map(category -> modelMapper.map(category, ChildCategoryDTO.class)).collect(Collectors.toList());
        }

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(pageCategories.getNumber());
        categoryResponse.setPageSize(pageCategories.getSize());
        categoryResponse.setTotalElements(pageCategories.getTotalElements());
        categoryResponse.setTotalPages(pageCategories.getTotalPages());
        categoryResponse.setLastPage(pageCategories.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(ChildCategoryDTO categoryDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setSlug(CreateSlug.toSlug(categoryDTO.getCategoryName()));
        if (categoryDTO.getParent() != null) {
            Category parentCategory = categoryRepo.findById(categoryDTO.getParent().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId",
                            categoryDTO.getParent().getCategoryId()));
            category.setParent(parentCategory);
        }
        String fileName = fileService.uploadImage(path, image);
        category.setImage(fileName);
        category.setStatus(false);

        category.setCreatedBy(userId);
        category.setUpdatedBy(userId);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepo.save(category);

        return modelMapper.map(category, ChildCategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(ChildCategoryDTO categoryDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Category category = categoryRepo.findById(categoryDTO.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category", "categoryId", categoryDTO.getCategoryId()));
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setSlug(CreateSlug.toSlug(categoryDTO.getCategoryName()));
        if (categoryDTO.getParent() != null) {
            if (categoryDTO.getCategoryId() == categoryDTO.getParent().getCategoryId()) {
                throw new APIException("Category cannot be its own parent");
            }
            Category parentCategory = categoryRepo.findById(categoryDTO.getParent().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId",
                            categoryDTO.getParent().getCategoryId()));
            category.setParent(parentCategory);
        } else {
            category.setParent(null);
        }
        if (image != null) {
            String fileName = fileService.uploadImage(path, image);
            category.setImage(fileName);
        }
        category.setStatus(categoryDTO.getStatus());

        category.setUpdatedBy(userId);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepo.save(category);

        return modelMapper.map(category, ChildCategoryDTO.class);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepo.delete(category);

        return "Category with ID: " + categoryId + " deleted successfully";
    }

}
