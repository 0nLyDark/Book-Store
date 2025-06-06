package com.dangphuoctai.BookStore.payloads.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.Post;
import com.dangphuoctai.BookStore.enums.PostType;

import jakarta.persistence.criteria.Predicate;

public class PostSpecification {
    public static Specification<Post> filter(
            Long topicId,
            PostType postType,
            Boolean status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Lọc theo topicId
            if (topicId != null) {
                predicates.add(cb.equal(root.get("topic").get("topicId"), topicId));
            }

            // Lọc theo Status
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Lọc theo PostType
            if (postType != null) {
                predicates.add(cb.equal(root.get("postType"), postType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
