package com.dangphuoctai.BookStore.payloads.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.Product;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<Product> filter(
            String isbn,
            Double minPrice,
            Double maxPrice,
            Boolean isSale,
            Boolean status,
            List<Long> categoryIds,
            String slugCategory,
            List<Long> authorIds,
            List<Long> languageIds,
            List<Long> supplierIds,
            List<Long> publisherIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
            if (isSale != null && isSale == true)
                predicates.add(cb.notEqual(root.get("discount"), 0));

            // Lọc theo trạng thái
            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            // Lọc theo categoryIds
            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.join("categories", JoinType.LEFT).get("categoryId").in(categoryIds));
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
            if (supplierIds != null && !supplierIds.isEmpty())
                predicates.add(root.get("supplier").get("supplierId").in(supplierIds));

            // Lọc theo publisher
            if (publisherIds != null && !publisherIds.isEmpty())
                predicates.add(root.get("publisher").get("publisherId").in(publisherIds));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> filterStock(
            String isbn,
            Boolean status,
            Integer qty) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo mã sản phẩm (isbn)
            if (isbn != null && !isbn.trim().isEmpty()) {
                predicates.add(cb.like(root.get("isbn"), "%" + isbn + "%"));
            }

            // Lọc theo trạng thái
            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            // Lọc theo số lượng tồn kho
            if (qty != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("quantity"), qty));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
