package com.dangphuoctai.BookStore.payloads.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.Category;
import com.dangphuoctai.BookStore.entity.File;
import com.dangphuoctai.BookStore.entity.Review;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ReviewSpecification {
    public static Specification<Review> filter(
            Long productId,
            Integer star,
            boolean isImage) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("product").get("productId"), productId));

            if (star != null) {
                predicates.add(cb.equal(root.get("star"), star));
            }
            if (isImage) {
                // JOIN với images và lọc các review có ít nhất 1 ảnh
                Join<Review, File> imagesJoin = root.join("images", JoinType.INNER);
                predicates.add(cb.isNotNull(imagesJoin.get("fileId"))); // đảm bảo có ít nhất 1 image
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
