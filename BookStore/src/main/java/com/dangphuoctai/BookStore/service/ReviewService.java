package com.dangphuoctai.BookStore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.Review.ReviewDTO;
import com.dangphuoctai.BookStore.payloads.dto.Review.ReviewRequest;
import com.dangphuoctai.BookStore.payloads.dto.Review.StarDTO;
import com.dangphuoctai.BookStore.payloads.response.ReviewResponse;

public interface ReviewService {
    ReviewDTO getReviewById(Long reviewId);

    ReviewDTO getReviewByOrderItemId(Long orderItemId);

    ReviewDTO createReviewByOrderItem(ReviewRequest reviewRequest, List<MultipartFile> images) throws IOException;

    ReviewDTO updateReviewByOrderItem(ReviewRequest reviewRequest, List<MultipartFile> images) throws IOException;

    ReviewResponse getAllReviewByProdcutId(Long productId, Integer star, Boolean isImage, Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder);

    StarDTO averageStarByProductId(Long productId);

}
