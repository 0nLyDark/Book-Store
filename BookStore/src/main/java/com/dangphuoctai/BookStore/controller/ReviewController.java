package com.dangphuoctai.BookStore.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.Review.ReviewDTO;
import com.dangphuoctai.BookStore.payloads.dto.Review.StarDTO;
import com.dangphuoctai.BookStore.payloads.response.ReviewResponse;
import com.dangphuoctai.BookStore.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/public/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long reviewId) {
        ReviewDTO result = reviewService.getReviewById(reviewId);
        return new ResponseEntity<ReviewDTO>(result, HttpStatus.OK);
    }

    @GetMapping("/public/reviews/orderItem/{orderItemId}")
    public ResponseEntity<ReviewDTO> getReviewByOrderItemId(@PathVariable Long orderItemId) {
        ReviewDTO result = reviewService.getReviewByOrderItemId(orderItemId);
        return new ResponseEntity<ReviewDTO>(result, HttpStatus.OK);
    }

    @PostMapping("/public/reviews/orderItem")
    public ResponseEntity<ReviewDTO> createReviewByOrderItem(
            @RequestParam(value = "files", required = false) List<MultipartFile> images,
            @ModelAttribute("review") ReviewDTO reviewDTO) throws IOException {

        ReviewDTO created = reviewService.createReviewByOrderItem(reviewDTO, images);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/public/reviews/orderItem")
    public ResponseEntity<ReviewDTO> updateReviewByOrderItem(
            @RequestParam(value = "files", required = false) List<MultipartFile> images,
            @ModelAttribute("review") ReviewDTO reviewDTO) throws IOException {

        ReviewDTO updated = reviewService.updateReviewByOrderItem(reviewDTO, images);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @GetMapping("/public/reviews/product/{productId}")
    public ResponseEntity<ReviewResponse> getAllReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(value = "star", required = false) Integer star,
            @RequestParam(value = "isImage", required = false) Boolean isImage,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "reviewId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        ReviewResponse response = reviewService.getAllReviewByProdcutId(
                productId, star, isImage, pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/public/reviews/product/{productId}/average-star")
    public ResponseEntity<StarDTO> getAverageStarByProductId(@PathVariable Long productId) {

        StarDTO result = reviewService.averageStarByProductId(productId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
