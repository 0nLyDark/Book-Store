package com.dangphuoctai.BookStore.payloads;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<Product> filter(
            String keyword,
            String isbn,
            Double minPrice,
            Double maxPrice,
            Boolean isSale,
            Boolean status,
            Long categoryId,
            String slugCategory,
            List<Long> authorIds,
            List<Long> languageIds,
            Long supplierId,
            Long publisherId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo tên sản phẩm (productName)
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
            }
            // Tìm theo mã sản phẩm (isbn)
            if (isbn != null && !isbn.trim().isEmpty()) {
                predicates.add(cb.like(root.get("isbn"), "%" + isbn + "%"));
            }
            // Lọc theo giá
            if (minPrice != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));

            // Lọc theo discount
            if (isSale == true)
                predicates.add(cb.notEqual(root.get("discount"), 0));

            // Lọc theo trạng thái
            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            // Lọc theo categoryId
            if (categoryId != null) {
                predicates.add(cb.equal(root.join("categories").get("categoryId"), categoryId));
            }

            // Lọc theo category slug
            if (slugCategory != null && !slugCategory.trim().isEmpty()) {
                predicates.add(cb.equal(root.join("categories").get("slug"), slugCategory));
            }

            // Lọc theo author
            if (authorIds != null && !authorIds.isEmpty())
                predicates.add(root.join("authors").get("authorId").in(authorIds));

            // Lọc theo language
            if (languageIds != null && !languageIds.isEmpty())
                predicates.add(root.join("languages").get("languageId").in(languageIds));

            // Lọc theo supplier
            if (supplierId != null)
                predicates.add(cb.equal(root.get("supplier").get("supplierId"), supplierId));

            // Lọc theo publisher
            if (publisherId != null)
                predicates.add(cb.equal(root.get("publisher").get("publisherId"), publisherId));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
