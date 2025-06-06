package com.dangphuoctai.BookStore.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.enums.PostType;
import com.dangphuoctai.BookStore.payloads.dto.TopicDTO;
import com.dangphuoctai.BookStore.payloads.dto.PostDTO.JsonToTopicDTOEditor;
import com.dangphuoctai.BookStore.payloads.dto.PostDTO.PostDTO;
import com.dangphuoctai.BookStore.payloads.response.PostResponse;
import com.dangphuoctai.BookStore.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private PostService postService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(TopicDTO.class, new JsonToTopicDTOEditor());
    }

    @GetMapping("/public/posts/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        PostDTO postDTO = postService.getPostById(postId);

        return new ResponseEntity<PostDTO>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/public/posts/slug/{slug}")
    public ResponseEntity<PostDTO> getPostBySlug(@PathVariable String slug) {
        PostDTO postDTO = postService.getPostBySlug(slug);

        return new ResponseEntity<PostDTO>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/public/posts")
    public ResponseEntity<PostResponse> getAllPost(
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "type", required = false) PostType type,
            @RequestParam(name = "topicId", required = false) Long topicId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_POSTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        PostResponse postResponse = postService.getAllPost(status, type, topicId,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "postId" : sortBy,
                sortOrder);

        return new ResponseEntity<PostResponse>(postResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/posts")
    public ResponseEntity<PostDTO> createPost(@RequestParam(value = "file", required = false) MultipartFile image,
            @RequestParam(value = "topic", required = false) TopicDTO topic,
            @ModelAttribute PostDTO post) throws IOException {
        post.setTopic(topic);

        PostDTO postDTO = postService.createPost(post, image);

        return new ResponseEntity<PostDTO>(postDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/posts")
    public ResponseEntity<PostDTO> updatePost(@RequestParam(value = "file", required = false) MultipartFile image,
            @RequestParam(value = "topic", required = false) TopicDTO topic,
            @ModelAttribute PostDTO post) throws IOException {
        post.setTopic(topic);

        PostDTO postDTO = postService.updatePost(post, image);

        return new ResponseEntity<PostDTO>(postDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        String result = postService.deletePost(postId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
