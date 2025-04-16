package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Topic;

@Repository
public interface TopicRepo extends JpaRepository<Topic, Long> {

    Optional<Topic> findBySlug(String slug);

    Page<Topic> findAllByStatus(Boolean status, Pageable pageDetails);

}
