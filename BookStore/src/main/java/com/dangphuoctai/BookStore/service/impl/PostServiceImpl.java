package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.entity.Post;
import com.dangphuoctai.BookStore.entity.Topic;
import com.dangphuoctai.BookStore.enums.PostType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.PostDTO;
import com.dangphuoctai.BookStore.payloads.response.PostResponse;
import com.dangphuoctai.BookStore.repository.PostRepo;
import com.dangphuoctai.BookStore.repository.TopicRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
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
        public PostResponse getAllPost(Boolean status, PostType type, Integer pageNumber, Integer pageSize,
                        String sortBy,
                        String sortOrder) {
                String field = String.format("status:%s|type:%s|page:%d|size:%d|sortBy:%s|sortOrder:%s",
                                status, type, pageNumber, pageSize, sortBy, sortOrder);
                PostResponse cached = (PostResponse) postResponseRedisService.hashGet(POST_CACHE_KEY, field);
                if (cached != null) {
                        return cached;
                }
                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
                Page<Post> pagePosts;
                if (status != null && type != null) {
                        pagePosts = postRepo.findAllByStatusAndType(status, type, pageDetails);
                } else if (status != null) {
                        pagePosts = postRepo.findAllByStatus(status, pageDetails);
                } else if (type != null) {
                        pagePosts = postRepo.findAllByType(type, pageDetails);
                } else {
                        pagePosts = postRepo.findAll(pageDetails);
                }

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
                postResponseRedisService.hashSet(POST_CACHE_KEY, field, postResponse);
                postResponseRedisService.setTimeToLiveOnce(POST_CACHE_KEY, 3, TimeUnit.HOURS);

                return postResponse;
        }

        @Override
        public PostDTO createPost(PostDTO postDTO) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Long userId = jwt.getClaim("userId");
                // Check if the topic exists
                if (postDTO.getTopic() == null || postDTO.getTopic().getTopicId() == null) {
                        throw new APIException(" Topic ID is required to create a post.");
                }
                Topic topic = topicRepo.findById(postDTO.getTopic().getTopicId())
                                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topicId",
                                                postDTO.getTopic().getTopicId()));
                Post post = new Post();
                post.setTitle(postDTO.getTitle());
                post.setSlug(CreateSlug.toSlug(postDTO.getTitle()));
                post.setContent(postDTO.getContent());
                post.setType(postDTO.getType());
                post.setStatus(false);
                post.setTopic(topic);
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
        public PostDTO updatePost(PostDTO postDTO) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Long userId = jwt.getClaim("userId");
                Topic topic = topicRepo.findById(postDTO.getTopic().getTopicId())
                                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topicId",
                                                postDTO.getTopic().getTopicId()));
                Post post = postRepo.findById(postDTO.getPostId())
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId",
                                                postDTO.getPostId()));
                post.setTitle(postDTO.getTitle());
                post.setSlug(CreateSlug.toSlug(postDTO.getTitle()));
                post.setContent(postDTO.getContent());
                post.setType(postDTO.getType());
                post.setStatus(postDTO.getStatus());
                post.setTopic(topic);
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

                return "Post with ID: " + postId + " deleted successfully";
        }
}
