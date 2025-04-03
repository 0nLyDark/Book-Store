package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.dto.PostDTO;
import com.dangphuoctai.BookStore.payloads.response.PostResponse;

public interface PostService {
    PostDTO getPostById(Long postId);

    PostDTO getPostBySlug(String slug);

    PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    PostDTO createPost(PostDTO PostDTO);

    PostDTO updatePost(PostDTO PostDTO);

    String deletePost(Long PostId);
}
