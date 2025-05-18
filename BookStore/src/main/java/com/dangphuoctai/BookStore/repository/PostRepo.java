package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Post;
import com.dangphuoctai.BookStore.enums.PostType;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    Page<Post> findAllByStatus(Boolean status, Pageable pageDetails);

    Page<Post> findAllByStatusAndType(Boolean status, PostType type, Pageable pageDetails);

    Page<Post> findAllByType(PostType type, Pageable pageDetails);

}
