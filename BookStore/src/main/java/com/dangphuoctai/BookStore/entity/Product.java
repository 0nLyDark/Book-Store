package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Tên sản phẩm phải có ít nhất 3 ký tự")
    private String productName;

    @Column(name = "search_text", columnDefinition = "TEXT")
    private String searchText;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, unique = true, length = 13)
    @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "ISBN phải có 10 hoặc 13 chữ số")
    private String isbn;

    @Column(nullable = false)
    private int weight;

    private String size;

    @Min(1)
    private int pageNumber;

    private String format;

    @Min(2000)
    private int year;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;

    @Min(0)
    @Max(100)
    private int discount = 0;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean status;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    @Size(min = 6, message = "Mô tả sản phẩm phải có ít nhất 6 ký tự")
    private String description;

    // @NotBlank
    // @Column(columnDefinition = "TEXT")
    // @Size(min = 6, message = "Product detail must contain atleast 6 characters")
    // private String detail;

    @OneToMany(mappedBy = "product", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<File> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "product_author", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "product_language", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<Language> languages = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Review> reviews = new ArrayList<>();

    @Column(nullable = false)
    private Long createdBy;
    @Column(nullable = false)
    private Long updatedBy;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateSave() {
        if (categories.size() == 0) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
        if (authors.size() == 0) {
            throw new IllegalArgumentException("Tác giả không được để trống");
        }
        if (languages.size() == 0) {
            throw new IllegalArgumentException("Ngôn ngữ không được để trống");
        }
        if (images.size() == 0) {
            throw new IllegalArgumentException("Hình ảnh không được để trống");
        }

    }
}
