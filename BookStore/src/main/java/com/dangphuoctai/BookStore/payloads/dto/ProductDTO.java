package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.CategoryDTO.CategoryDTO;
import com.dangphuoctai.BookStore.payloads.dto.PublisherDTO.PublisherDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private String slug;
    private String isbn;
    private String weight;
    private String size;
    private int year;
    private int quantity;
    private double price;
    private int discount;
    private int pageNumber;
    private String description;
    private Boolean status;

    private List<FileDTO> images;
    private List<CategoryDTO> categories;
    private List<AuthorDTO> authors;
    private List<LanguageDTO> languages;

    private PublisherDTO publisher;
    private SupplierDTO supplier;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
