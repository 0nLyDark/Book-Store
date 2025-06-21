package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.entity.Post;
import com.dangphuoctai.BookStore.entity.Promotion;
import com.dangphuoctai.BookStore.entity.Topic;
import com.dangphuoctai.BookStore.enums.PostType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.Specification.PostSpecification;
import com.dangphuoctai.BookStore.payloads.Specification.PromotionSpecification;
import com.dangphuoctai.BookStore.payloads.dto.PostDTO.PostDTO;
import com.dangphuoctai.BookStore.payloads.response.PostResponse;
import com.dangphuoctai.BookStore.repository.PostRepo;
import com.dangphuoctai.BookStore.repository.TopicRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.service.PostService;
import com.dangphuoctai.BookStore.utils.CreateSlug;

@Service
public class PostServiceImpl implements PostService {

        @Autowired
        private TopicRepo topicRepo;

        @Autowired
        private PostRepo postRepo;

        @Autowired
        private ModelMapper modelMapper;

        @Autowired
        private FileService fileService;

        @Value("${project.image}")
        private String path;

        @Autowired
        private BaseRedisService<String, String, PostDTO> postRedisService;

        @Autowired
        private BaseRedisService<String, String, PostResponse> postResponseRedisService;

        private static final String POST_CACHE_KEY = "post";
        private static final String POST_PAGE_CACHE_KEY = "post:pages";

        @Override
        public PostDTO getPostById(Long postId) {
                String field = "id:" + postId;
                PostDTO cached = (PostDTO) postRedisService.hashGet(POST_CACHE_KEY, field);
                if (cached != null) {
                        return cached;
                }
                Post post = postRepo.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));
                PostDTO postDTO = modelMapper.map(post, PostDTO.class);

                // Save cache post to redis
                postRedisService.hashSet(POST_CACHE_KEY, field, postDTO);
                postRedisService.setTimeToLiveOnce(POST_CACHE_KEY, 3, TimeUnit.HOURS);

                return postDTO;
        }

        @Override
        public List<PostDTO> getManyPostById(List<Long> postIds) {
                List<Post> posts = postRepo.findAllById(postIds);
                if (posts.size() != postIds.size()) {
                        throw new ResourceNotFoundException("Post", "postId", postIds);
                }
                List<PostDTO> postDTOs = posts.stream()
                                .map(post -> modelMapper.map(post, PostDTO.class))
                                .collect(Collectors.toList());
                return postDTOs;
        }

        @Override
        public PostDTO getPostBySlug(String slug) {
                String field = "slug:" + slug;
                PostDTO cached = (PostDTO) postRedisService.hashGet(POST_CACHE_KEY, field);
                if (cached != null) {
                        return cached;
                }
                Post post = postRepo.findBySlug(slug)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));

                PostDTO postDTO = modelMapper.map(post, PostDTO.class);

                // Save cache post to redis
                postRedisService.hashSet(POST_CACHE_KEY, field, postDTO);
                postRedisService.setTimeToLiveOnce(POST_CACHE_KEY, 3, TimeUnit.HOURS);

                return postDTO;
        }

        @Override
        public PostResponse getAllPost(Boolean status, PostType type, Long topicId, String slugTopic,
                        Integer pageNumber,
                        Integer pageSize,
                        String sortBy,
                        String sortOrder) {
                String field = String.format(
                                "status:%s|type:%s|topicId:%s|slugTopic:%s|page:%d|size:%d|sortBy:%s|sortOrder:%s",
                                status, type, topicId, slugTopic, pageNumber, pageSize, sortBy, sortOrder);
                PostResponse cached = (PostResponse) postResponseRedisService.hashGet(POST_PAGE_CACHE_KEY, field);
                if (cached != null) {
                        return cached;
                }
                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
                Specification<Post> postSpecification = PostSpecification.filter(topicId, slugTopic, type, status);

                Page<Post> pagePosts = postRepo.findAll(postSpecification, pageDetails);

                List<PostDTO> postDTOs = pagePosts.getContent().stream()
                                .map(post -> modelMapper.map(post, PostDTO.class))
                                .collect(Collectors.toList());

                PostResponse postResponse = new PostResponse();
                postResponse.setContent(postDTOs);
                postResponse.setPageNumber(pagePosts.getNumber());
                postResponse.setPageSize(pagePosts.getSize());
                postResponse.setTotalElements(pagePosts.getTotalElements());
                postResponse.setTotalPages(pagePosts.getTotalPages());
                postResponse.setLastPage(pagePosts.isLast());

                // Save cache post to redis
                postResponseRedisService.hashSet(POST_PAGE_CACHE_KEY, field, postResponse);
                postResponseRedisService.setTimeToLiveOnce(POST_PAGE_CACHE_KEY, 3, TimeUnit.HOURS);

                return postResponse;
        }

        @Override
        public PostDTO createPost(PostDTO postDTO, MultipartFile image) throws IOException {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Long userId = jwt.getClaim("userId");
                Post post = new Post();
                // Check if the topic exists
                if (PostType.POST.equals(postDTO.getType())) {
                        if (postDTO.getTopic() == null || postDTO.getTopic().getTopicId() == null) {
                                throw new APIException("Cần chọn chủ đề để tạo bài viết.");
                        }
                        Topic topic = topicRepo.findById(postDTO.getTopic().getTopicId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Topic", "topicId",
                                                        postDTO.getTopic().getTopicId()));
                        post.setTopic(topic);
                }

                post.setTitle(postDTO.getTitle());
                post.setSlug(CreateSlug.toSlug(postDTO.getTitle()));
                if (image != null) {
                        String fileName = fileService.uploadImage(path, image);
                        post.setImage(fileName);
                }
                post.setContent(postDTO.getContent());
                post.setType(postDTO.getType());
                post.setStatus(false);
                post.setCreatedBy(userId);
                post.setUpdatedBy(userId);
                post.setCreatedAt(LocalDateTime.now());
                post.setUpdatedAt(LocalDateTime.now());
                postRepo.save(post);
                PostDTO postRes = modelMapper.map(post, PostDTO.class);
                // Save cache post to redis
                String field = "id:" + post.getPostId();
                String fieldSlug = "slug:" + post.getPostId();
                postRedisService.hashSet(POST_CACHE_KEY, field, postRes);
                postRedisService.hashSet(POST_CACHE_KEY, fieldSlug, postRes);
                postRedisService.setTimeToLiveOnce(POST_CACHE_KEY, 3, TimeUnit.HOURS);
                postResponseRedisService.delete(POST_PAGE_CACHE_KEY);

                return postRes;
        }

        @Override
        public PostDTO updatePost(PostDTO postDTO, MultipartFile image) throws IOException {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Long userId = jwt.getClaim("userId");
                Post post = postRepo.findById(postDTO.getPostId())
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId",
                                                postDTO.getPostId()));
                if (PostType.POST.equals(postDTO.getType())) {
                        Topic topic = topicRepo.findById(postDTO.getTopic().getTopicId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Topic", "topicId",
                                                        postDTO.getTopic().getTopicId()));
                        post.setTopic(topic);
                }

                post.setTitle(postDTO.getTitle());
                post.setSlug(CreateSlug.toSlug(postDTO.getTitle()));
                if (image != null) {
                        String fileName = fileService.uploadImage(path, image);
                        post.setImage(fileName);
                }
                post.setContent(postDTO.getContent());
                post.setType(postDTO.getType());
                post.setStatus(postDTO.getStatus());
                post.setUpdatedBy(userId);
                post.setUpdatedAt(LocalDateTime.now());
                postRepo.save(post);

                PostDTO postRes = modelMapper.map(post, PostDTO.class);
                // Save cache post to redis
                String field = "id:" + post.getPostId();
                String fieldSlug = "slug:" + post.getPostId();
                postRedisService.hashSet(POST_CACHE_KEY, field, postRes);
                postRedisService.hashSet(POST_CACHE_KEY, fieldSlug, postRes);
                postRedisService.setTimeToLiveOnce(POST_CACHE_KEY, 3, TimeUnit.HOURS);
                postResponseRedisService.delete(POST_PAGE_CACHE_KEY);

                return postRes;
        }

        @Override
        public String deletePost(Long postId) {
                Post post = postRepo.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));
                String field = "id:" + post.getPostId();
                String fieldSlug = "slug:" + post.getPostId();
                postRepo.delete(post);
                // Delete cache post to redis
                postRedisService.delete(POST_CACHE_KEY, field);
                postRedisService.delete(POST_CACHE_KEY, fieldSlug);
                postResponseRedisService.delete(POST_PAGE_CACHE_KEY);

                return "Xóa bài viết với ID: " + postId + " thành công";
        }
}
