package com.dangphuoctai.BookStore.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CacheKeyGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String generateProductCacheKey(
            String keyword, String isbn, Double minPrice, Double maxPrice,
            Boolean isSale, Boolean isNew, Long categoryId, String slugCategory,
            List<Long> authorIds, List<Long> languageIds,
            List<Long> supplierId, List<Long> publisherId, Boolean status,
            Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("keyword", keyword);
            params.put("isbn", isbn);
            params.put("minPrice", minPrice);
            params.put("maxPrice", maxPrice);
            params.put("isSale", isSale);
            params.put("isNew", isNew);
            params.put("slugCategory", slugCategory);
            params.put("categoryId", categoryId);
            params.put("authorIds", authorIds);
            params.put("languageIds", languageIds);
            params.put("supplierId", supplierId);
            params.put("publisherId", publisherId);
            params.put("status", status);
            params.put("pageNumber", pageNumber);
            params.put("pageSize", pageSize);
            params.put("sortBy", sortBy);
            params.put("sortOrder", sortOrder);

            // Chuyển map thành chuỗi JSON
            String json = objectMapper.writeValueAsString(params);

            // Hash MD5 để tạo key gọn
            return DigestUtils.md5DigestAsHex(json.getBytes());

        } catch (Exception e) {
            throw new RuntimeException("Error generating cache key", e);
        }
    }
}
