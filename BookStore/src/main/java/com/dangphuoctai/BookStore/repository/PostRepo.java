package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Post;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    Page<Post> findAll(Specification<Post> postSpecification, Pageable pageDetails);

}
