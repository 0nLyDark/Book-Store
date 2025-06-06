package com.dangphuoctai.BookStore.payloads.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.Promotion;
import com.dangphuoctai.BookStore.enums.PromotionType;

import jakarta.persistence.criteria.Predicate;

public class PromotionSpecification {
    public static Specification<Promotion> filter(
            PromotionType promotionType,
            Boolean status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo Status
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Lọc theo PromotionType
            if (promotionType != null) {
                predicates.add(cb.equal(root.get("promotionType"), promotionType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
