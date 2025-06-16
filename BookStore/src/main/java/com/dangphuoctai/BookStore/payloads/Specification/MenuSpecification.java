package com.dangphuoctai.BookStore.payloads.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.Menu;

import jakarta.persistence.criteria.Predicate;

public class MenuSpecification {
    public static Specification<Menu> filter(
            String keyword,
            String type,
            Boolean status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Tìm theo tên danh mục (categoryName)
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" +
                        keyword.toLowerCase() + "%"));
            }
            if (type != null && type.equalsIgnoreCase("parent")) {
                predicates.add(cb.isNull(root.get("parent")));
            }
            // // Lọc theo Status
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
