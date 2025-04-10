package com.dangphuoctai.BookStore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.ProductDTO;
import com.dangphuoctai.BookStore.payloads.response.ProductResponse;

public interface ProductService {

        ProductDTO getProductById(Long productId);

        ProductDTO getProductBySlug(String slug);

        ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

        ProductDTO createProduct(ProductDTO productDTO, List<MultipartFile> images, List<Long> categoryIds,
                        List<Long> authorIds, List<Long> languageIds, Long supplierId, Long publisherId)
                        throws IOException;

        ProductDTO updateProduct(ProductDTO productDTO, List<MultipartFile> images, List<Long> removeImage,
                        List<Long> categoryIds,
                        List<Long> authorIds, List<Long> languageIds, Long supplierId, Long publisherId)
                        throws IOException;

        String deleteProduct(Long productId);

}
