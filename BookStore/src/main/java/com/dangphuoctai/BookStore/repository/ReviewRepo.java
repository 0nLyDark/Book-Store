package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Review;
import com.dangphuoctai.BookStore.payloads.dto.Review.StarDTO;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {

    boolean existsByOrderItemOrderItemId(Long orderItemId);

    Optional<Review> findByOrderItemOrderItemId(Long orderItemId);

    Page<Review> findAll(Specification<Review> reviewSpecification, Pageable pageDetails);

    @Query("""
                SELECT new com.dangphuoctai.BookStore.payloads.dto.Review.StarDTO(
                    COALESCE(AVG(r.star), 0),
                    COUNT(r)
                )
                FROM Review r
                WHERE r.product.productId = :productId
            """)
    StarDTO getStarsByProductId(Long productId);

}
