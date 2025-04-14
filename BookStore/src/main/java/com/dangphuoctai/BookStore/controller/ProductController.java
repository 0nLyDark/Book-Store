package com.dangphuoctai.BookStore.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.ProductDTO;
import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.CategoryDTO;
import com.dangphuoctai.BookStore.payloads.response.ProductResponse;
import com.dangphuoctai.BookStore.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        ProductDTO productDTO = productService.getProductById(productId);

        return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
    }

    @GetMapping("/public/products/slug/{slug}")
    public ResponseEntity<ProductDTO> getProductBySlug(@PathVariable String slug) {
        ProductDTO productDTO = productService.getProductBySlug(slug);

        return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
    }

    // @GetMapping("/public/products")
    // public ResponseEntity<ProductResponse> getAllProducts(
    // @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,
    // required = false) Integer pageNumber,
    // @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE,
    // required = false) Integer pageSize,
    // @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY,
    // required = false) String sortBy,
    // @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR,
    // required = false) String sortOrder) {
    // ProductResponse productResponse = productService.getAllProducts(pageNumber ==
    // 0 ? pageNumber : pageNumber - 1,
    // pageSize,
    // "id".equals(sortBy) ? "productId" : sortBy,
    // sortOrder);

    // return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    // }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "isbn", required = false) String isbn,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "authorIds", required = false) List<Long> authorIds,
            @RequestParam(name = "languageIds", required = false) List<Long> languageIds,
            @RequestParam(name = "supplierId", required = false) Long supplierId,
            @RequestParam(name = "publisherId", required = false) Long publisherId,
            @RequestParam(name = "isSale", defaultValue = "false", required = false) Boolean isSale,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(
                keyword,isbn, minPrice, maxPrice, isSale,
                categoryId, authorIds, languageIds, supplierId, publisherId,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "productId" : sortBy,
                sortOrder);

        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/staff/products")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestParam(value = "files") List<MultipartFile> images,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
            @RequestParam(value = "languageIds", required = false) List<Long> languageIds,
            @RequestParam("supplierId") Long supplierId,
            @RequestParam("publisherId") Long publisherId,
            @ModelAttribute ProductDTO productDTO) throws IOException {

        ProductDTO createdProduct = productService.createProduct(productDTO, images,
                categoryIds, authorIds,
                languageIds, supplierId, publisherId);

        return new ResponseEntity<ProductDTO>(createdProduct, HttpStatus.CREATED);

    }

    @PutMapping("/staff/products")
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestParam(value = "files", required = false) List<MultipartFile> images,
            @RequestParam(value = "removefiles", required = false) List<Long> removeImage,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
            @RequestParam(value = "languageIds", required = false) List<Long> languageIds,
            @RequestParam("supplierId") Long supplierId,
            @RequestParam("publisherId") Long publisherId,
            @ModelAttribute ProductDTO productDTO) throws IOException {

        ProductDTO createdProduct = productService.updateProduct(productDTO, images, removeImage, categoryIds,
                authorIds,
                languageIds, supplierId, publisherId);

        return new ResponseEntity<ProductDTO>(createdProduct, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String message = productService.deleteProduct(productId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

}
