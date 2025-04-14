package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
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
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.PostDTO;
import com.dangphuoctai.BookStore.payloads.response.PostResponse;
import com.dangphuoctai.BookStore.repository.PostRepo;
import com.dangphuoctai.BookStore.repository.TopicRepo;
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

        @Override
        public PostDTO getPostById(Long postId) {
                Post post = postRepo.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

                return modelMapper.map(post, PostDTO.class);
        }

        @Override
        public PostDTO getPostBySlug(String slug) {
                Post post = postRepo.findBySlug(slug)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));

                return modelMapper.map(post, PostDTO.class);
        }

        @Override
        public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
                Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

                Page<Post> pagePosts = postRepo.findAll(pageDetails);
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

                return modelMapper.map(post, PostDTO.class);
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

                return modelMapper.map(post, PostDTO.class);
        }

        @Override
        public String deletePost(Long postId) {
                Post post = postRepo.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));
                postRepo.delete(post);

                return "Post with ID: " + postId + " deleted successfully";
        }
}
