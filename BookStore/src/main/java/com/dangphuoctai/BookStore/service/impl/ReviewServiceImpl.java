package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.entity.File;
import com.dangphuoctai.BookStore.entity.OrderItem;
import com.dangphuoctai.BookStore.entity.Publisher;
import com.dangphuoctai.BookStore.entity.Review;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.enums.FileType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.Specification.PublisherSpecification;
import com.dangphuoctai.BookStore.payloads.Specification.ReviewSpecification;
import com.dangphuoctai.BookStore.payloads.dto.Review.ReviewDTO;
import com.dangphuoctai.BookStore.payloads.dto.Review.StarDTO;
import com.dangphuoctai.BookStore.payloads.response.ReviewResponse;
import com.dangphuoctai.BookStore.repository.OrderItemRepo;
import com.dangphuoctai.BookStore.repository.ReviewRepo;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.service.ReviewService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));

        ReviewDTO reviewRes = modelMapper.map(review, ReviewDTO.class);
        return reviewRes;
    }

    @Override
    public ReviewDTO getReviewByOrderItemId(Long orderItemId) {
        Review review = reviewRepo.findByOrderItemOrderItemId(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "orderItemId", orderItemId));

        ReviewDTO reviewRes = modelMapper.map(review, ReviewDTO.class);
        return reviewRes;
    }

    @Override
    public StarDTO averageStarByProductId(Long productId) {
        StarDTO star = reviewRepo.getStarsByProductId(productId);

        return star;
    }

    @Override
    public ReviewResponse getAllReviewByProdcutId(Long productId, Integer star, Boolean isImage, Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Review> reviewSpecification = ReviewSpecification.filter(productId, star, isImage);
        Page<Review> pageReviews = reviewRepo.findAll(reviewSpecification, pageDetails);
        List<ReviewDTO> reviewDTOs = pageReviews.getContent().stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());

        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setContent(reviewDTOs);
        reviewResponse.setPageNumber(pageReviews.getNumber());
        reviewResponse.setPageSize(pageReviews.getSize());
        reviewResponse.setTotalElements(pageReviews.getTotalElements());
        reviewResponse.setTotalPages(pageReviews.getTotalPages());
        reviewResponse.setLastPage(pageReviews.isLast());

        return reviewResponse;
    }

    @Override
    public ReviewDTO createReviewByOrderItem(ReviewDTO reviewDTO, List<MultipartFile> images) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Long orderItemId = reviewDTO.getOrderItemId();
        if (orderItemId == null) {
            throw new APIException("Không có thông tin orderItemId");
        }
        if (reviewRepo.existsByOrderItemOrderItemId(orderItemId)) {
            throw new APIException("Đánh giá của sản phẩm trong đơn hàng đã tồn tại");
        }
        OrderItem orderItem = orderItemRepo.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "orderItemId", orderItemId));
        User user = orderItem.getOrder().getUser();
        if (userId != user.getUserId()) {
            throw new AccessDeniedException("Bạn không có quyền tạo đánh giá cho sản phẩm của đơn hàng này");
        }
        Review review = new Review();
        review.setFullName(user.getFullName());
        review.setAvatar(user.getAvatar());
        review.setStar(reviewDTO.getStar());
        review.setComment(reviewDTO.getComment());
        review.setProduct(orderItem.getProduct());
        review.setOrderItem(orderItem);
        for (MultipartFile image : images) {
            String fileName = fileService.uploadImage(path, image);
            File file = new File();
            file.setFileName(fileName);
            file.setType(FileType.IMAGE);
            file.setReview(review);
            review.getImages().add(file);
        }
        review.setCreatedAt(LocalDateTime.now());
        reviewRepo.save(review);

        ReviewDTO reviewRes = modelMapper.map(review, ReviewDTO.class);
        return reviewRes;
    }

    @Override
    public ReviewDTO updateReviewByOrderItem(ReviewDTO reviewDTO, List<MultipartFile> images) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Long reviewId = reviewDTO.getReviewId();
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));
        if (userId != review.getOrderItem().getOrder().getUserId()) {
            throw new AccessDeniedException("Bạn không có quyền tạo đánh giá cho sản phẩm của đơn hàng này");
        }
        if (review.getUpdateAt() != null) {
            throw new APIException("Đánh giá chỉ được chỉnh sửa 1 lần, bạn đã thực hiện chỉnh sửa rồi!");
        }
        review.setStar(reviewDTO.getStar());
        review.setComment(reviewDTO.getComment());
        review.getImages().clear();
        for (MultipartFile image : images) {
            String fileName = fileService.uploadImage(path, image);
            File file = new File();
            file.setFileName(fileName);
            file.setType(FileType.IMAGE);
            file.setReview(review);
            review.getImages().add(file);
        }
        review.setUpdateAt(LocalDateTime.now());
        reviewRepo.save(review);

        ReviewDTO reviewRes = modelMapper.map(review, ReviewDTO.class);
        return reviewRes;
    }
}
