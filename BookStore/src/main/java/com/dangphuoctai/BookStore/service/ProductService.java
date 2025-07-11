package com.dangphuoctai.BookStore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.ProductDTO;
import com.dangphuoctai.BookStore.payloads.response.ProductResponse;

public interface ProductService {

        ProductDTO getProductById(Long productId);

        List<ProductDTO> getManyProductById(List<Long> productIds);

        ProductDTO getProductBySlug(String slug);

        // ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String
        // sortBy, String sortOrder);

        ProductResponse getAllProducts(String keyword, String isbn, Double minPrice, Double maxPrice,
                        Boolean isSale, Boolean isNew, Long categoryId, String slugCategory,
                        List<Long> authorIds, List<Long> languageIds,
                        List<Long> supplierIds, List<Long> publisherIds, Boolean status,
                        Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

        ProductDTO createProduct(ProductDTO productDTO, List<MultipartFile> images, List<Long> categoryIds,
                        List<Long> authorIds, List<Long> languageIds, Long supplierId, Long publisherId)
                        throws IOException;

        ProductDTO updateProduct(ProductDTO productDTO, List<Long> oldImages, List<MultipartFile> images,
                        List<Long> categoryIds,
                        List<Long> authorIds, List<Long> languageIds, Long supplierId, Long publisherId)
                        throws IOException;

        String deleteProduct(Long productId);

}
