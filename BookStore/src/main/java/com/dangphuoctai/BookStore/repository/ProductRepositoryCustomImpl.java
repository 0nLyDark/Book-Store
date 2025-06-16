package com.dangphuoctai.BookStore.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.utils.CreateSlug;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Order;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Product> fullTextSearchWithFilters(String keyword, Specification<Product> spec, Pageable pageable) {
        String processedKeyword = CreateSlug.removeAccents(keyword);

        // 1. FULL-TEXT SEARCH native SQL
        String nativeSql = """
                    SELECT p.product_id FROM products p
                    WHERE MATCH(p.search_text) AGAINST (:keyword IN BOOLEAN MODE)
                """;

        List<Long> matchedIds = entityManager.createNativeQuery(nativeSql)
                .setParameter("keyword", processedKeyword + "*") // ví dụ: ao* nu*
                .getResultList();

        if (matchedIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. Dùng Specification và lọc bằng matchedIds
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(root.get("productId").in(matchedIds));

        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(root, query, cb);
            if (specPredicate != null) {
                predicates.add(specPredicate);
            }
        }

        query.select(root)
                .where(cb.and(predicates.toArray(new Predicate[0])));

        // Sắp xếp
        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ? cb.asc(root.get(order.getProperty()))
                            : cb.desc(root.get(order.getProperty())))
                    .collect(Collectors.toList());
            query.orderBy(orders);
        }

        List<Product> content = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // 3. Đếm tổng số kết quả
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(countRoot.get("productId").in(matchedIds));

        if (spec != null) {
            Predicate specPredicate = spec.toPredicate(countRoot, countQuery, cb);
            if (specPredicate != null) {
                countPredicates.add(specPredicate);
            }
        }

        countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

}