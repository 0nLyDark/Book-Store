package com.dangphuoctai.BookStore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.enums.PostType;
import com.dangphuoctai.BookStore.payloads.dto.PostDTO.PostDTO;
import com.dangphuoctai.BookStore.payloads.response.PostResponse;

public interface PostService {
    PostDTO getPostById(Long postId);

    PostDTO getPostBySlug(String slug);

    List<PostDTO> getManyPostById(List<Long> postIds);

    PostResponse getAllPost(Boolean status, PostType type, Long topicId, String slugTopic, Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder);

    PostDTO createPost(PostDTO PostDTO, MultipartFile image) throws IOException;

    PostDTO updatePost(PostDTO PostDTO, MultipartFile image) throws IOException;

    String deletePost(Long PostId);
}
