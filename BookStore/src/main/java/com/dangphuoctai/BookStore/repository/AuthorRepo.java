package com.dangphuoctai.BookStore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Author;

@Repository
public interface AuthorRepo extends JpaRepository<Author, Long> {

    Page<Author> findAllByStatus(Boolean status,Pageable pageDetails);

}
