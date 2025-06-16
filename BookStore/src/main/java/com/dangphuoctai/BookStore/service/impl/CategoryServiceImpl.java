package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.entity.Category;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.Specification.CategorySpecification;
import com.dangphuoctai.BookStore.payloads.Specification.ProductSpecification;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.CategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.ChildCategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.ParentCategoryDTO;
import com.dangphuoctai.BookStore.payloads.response.CategoryResponse;
import com.dangphuoctai.BookStore.repository.CategoryRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.CategoryService;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.utils.CreateSlug;

import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    private BaseRedisService<String, String, ChildCategoryDTO> childCategoryRedisService;

    @Autowired
    private BaseRedisService<String, String, CategoryResponse> categoryResponseRedisService;

    private static final String CATEGORY_CACHE_KEY = "category";
    private static final String CATEGORY_PAGE_CACHE_KEY = "category:pages";

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        String field = "id:" + categoryId;
        ChildCategoryDTO cached = (ChildCategoryDTO) childCategoryRedisService.hashGet(CATEGORY_CACHE_KEY, field);
        if (cached != null) {
            return cached;
        }
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        ChildCategoryDTO categoryDTO = modelMapper.map(category, ChildCategoryDTO.class);

        // Save cache category to redis
        childCategoryRedisService.hashSet(CATEGORY_CACHE_KEY, field, categoryDTO);
        childCategoryRedisService.setTimeToLiveOnce(CATEGORY_CACHE_KEY, 6, TimeUnit.HOURS);

        return categoryDTO;
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
        String field = "slug:" + slug;
        ChildCategoryDTO cached = (ChildCategoryDTO) childCategoryRedisService.hashGet(CATEGORY_CACHE_KEY, field);
        if (cached != null) {
            return cached;
        }
        Category category = categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));

        ChildCategoryDTO categoryDTO = modelMapper.map(category, ChildCategoryDTO.class);

        // Save cache category to redis
        childCategoryRedisService.hashSet(CATEGORY_CACHE_KEY, field, categoryDTO);
        childCategoryRedisService.setTimeToLiveOnce(field, 6, TimeUnit.HOURS);

        return categoryDTO;
    }

    @Override
    public CategoryResponse getAllCategories(String keyword, Boolean status, String type, Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {
        String field = String.format("keyword:%s|status:%s|type:%s|page:%d|size:%d|sortBy:%s|sortOrder:%s",
                keyword, status, type, pageNumber, pageSize, sortBy, sortOrder);
        CategoryResponse cached = (CategoryResponse) categoryResponseRedisService.hashGet(CATEGORY_PAGE_CACHE_KEY,
                field);
        if (cached != null) {
            return cached;
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Category> categorySpecification = CategorySpecification.filter(keyword, type, status);
        // Get category
        Page<Category> pageCategories = categoryRepo.findAll(categorySpecification, pageDetails);
        List<Long> categoryIds = pageCategories.getContent()
                .stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());
        // Count products to categorys
        List<Object[]> counts = productRepo.countProductsByCategoryIds(categoryIds);
        Map<Long, Long> totalMap = counts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));

        List<Object[]> activeCounts = productRepo.countActiveProductsByCategoryIds(categoryIds);
        Map<Long, Long> activeMap = activeCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));
        // Map list category
        List<CategoryDTO> categoryDTOs;
        if (type.equalsIgnoreCase("parent")) {
            categoryDTOs = pageCategories.getContent().stream()
                    .map(category -> modelMapper.map(category, ParentCategoryDTO.class)).collect(Collectors.toList());
        } else {
            categoryDTOs = pageCategories.getContent().stream()
                    .map(category -> modelMapper.map(category, ChildCategoryDTO.class)).collect(Collectors.toList());
        }
        // Set totalproducts to category
        categoryDTOs.forEach(category -> {
            Long id = category.getCategoryId();
            Long total = totalMap.get(id);
            Long active = activeMap.get(id);
            category.setTotalProducts(total != null ? total : 0L);
            category.setActiveProducts(active != null ? active : 0L);
        });

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(pageCategories.getNumber());
        categoryResponse.setPageSize(pageCategories.getSize());
        categoryResponse.setTotalElements(pageCategories.getTotalElements());
        categoryResponse.setTotalPages(pageCategories.getTotalPages());
        categoryResponse.setLastPage(pageCategories.isLast());

        // Save cache category to redis
        categoryResponseRedisService.hashSet(CATEGORY_PAGE_CACHE_KEY, field, categoryResponse);
        categoryResponseRedisService.setTimeToLiveOnce(CATEGORY_PAGE_CACHE_KEY, 3, TimeUnit.HOURS);
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
        if (categoryDTO.getParent() != null && categoryDTO.getParent().getCategoryId() != null) {
            Category parentCategory = categoryRepo.findById(categoryDTO.getParent().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId",
                            categoryDTO.getParent().getCategoryId()));
            category.setParent(parentCategory);
        }
        if (image != null) {
            String fileName = fileService.uploadImage(path, image);
            category.setImage(fileName);
        }
        category.setStatus(false);

        category.setCreatedBy(userId);
        category.setUpdatedBy(userId);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepo.save(category);

        ChildCategoryDTO categoryRes = modelMapper.map(category, ChildCategoryDTO.class);
        // Save cache category to redis
        String field = "id:" + category.getCategoryId();
        String fieldSlug = "slug:" + category.getSlug();
        childCategoryRedisService.hashSet(CATEGORY_CACHE_KEY, field, categoryRes);
        childCategoryRedisService.hashSet(CATEGORY_CACHE_KEY, fieldSlug, categoryRes);
        childCategoryRedisService.setTimeToLive(CATEGORY_CACHE_KEY, 6, TimeUnit.HOURS);
        categoryResponseRedisService.delete(CATEGORY_PAGE_CACHE_KEY);

        return categoryRes;
    }

    private void getListChild(Category parentCategory, List<Long> childCategoryIds) {
        if (parentCategory == null || parentCategory.getChildrens() == null) {
            return;
        }
        for (Category childCategory : parentCategory.getChildrens()) {
            childCategoryIds.add(childCategory.getCategoryId());
            getListChild(childCategory, childCategoryIds);
        }

    }

    @Override
    public CategoryDTO updateCategory(ChildCategoryDTO categoryDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Category category = categoryRepo.findById(categoryDTO.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Danh mục", "categoryId", categoryDTO.getCategoryId()));
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setSlug(CreateSlug.toSlug(categoryDTO.getCategoryName()));
        if (categoryDTO.getParent() != null && categoryDTO.getParent().getCategoryId() != null) {
            if (categoryDTO.getCategoryId() == categoryDTO.getParent().getCategoryId()) {
                throw new APIException("Danh mục không thể là cha của chính nó");
            }
            Category parentCategory = categoryRepo.findById(categoryDTO.getParent().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Danh mục", "categoryId",
                            categoryDTO.getParent().getCategoryId()));
            List<Long> childCategoryIds = new ArrayList<>();
            getListChild(category, childCategoryIds);
            if (childCategoryIds.contains(parentCategory.getCategoryId())) {
                throw new APIException("Không thể đặt danh mục con làm cha của chính nó");
            }
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

        ChildCategoryDTO categoryRes = modelMapper.map(category, ChildCategoryDTO.class);
        // Save cache category to redis
        String field = CATEGORY_CACHE_KEY + category.getCategoryId();
        String fieldSlug = CATEGORY_CACHE_KEY + category.getSlug();
        childCategoryRedisService.hashSet(CATEGORY_CACHE_KEY, field, categoryRes);
        childCategoryRedisService.hashSet(CATEGORY_CACHE_KEY, fieldSlug, categoryRes);
        childCategoryRedisService.setTimeToLive(CATEGORY_CACHE_KEY, 6, TimeUnit.HOURS);
        categoryResponseRedisService.delete(CATEGORY_PAGE_CACHE_KEY);

        return categoryRes;
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        // Save cache category to redis
        String field = "id:" + category.getCategoryId();
        String fieldSlug = "slug:" + category.getSlug();

        categoryRepo.delete(category);

        // Delete cache category from redis
        childCategoryRedisService.delete(CATEGORY_CACHE_KEY, field);
        childCategoryRedisService.delete(CATEGORY_CACHE_KEY, fieldSlug);
        categoryResponseRedisService.delete(CATEGORY_PAGE_CACHE_KEY);

        return "Category with ID: " + categoryId + " deleted successfully";
    }

}
