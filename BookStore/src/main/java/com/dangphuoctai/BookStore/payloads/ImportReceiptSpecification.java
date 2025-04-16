package com.dangphuoctai.BookStore.payloads;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dangphuoctai.BookStore.entity.ImportReceipt;

import jakarta.persistence.criteria.Predicate;

public class ImportReceiptSpecification {
    public static Specification<ImportReceipt> filter(
            Long userId,
            Long supplierId,
            Boolean status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Tìm theo userId
            if (userId != null) {
                predicates.add(cb.equal(root.get("createdBy"), userId));
            }

            // Lọc theo supplier
            if (supplierId != null)
                predicates.add(cb.equal(root.get("supplier").get("supplierId"), supplierId));

            // Lọc theo trạng thái
            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
