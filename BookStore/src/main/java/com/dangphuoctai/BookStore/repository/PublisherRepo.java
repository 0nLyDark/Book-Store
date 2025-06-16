package com.dangphuoctai.BookStore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Publisher;

@Repository
public interface PublisherRepo extends JpaRepository<Publisher, Long> {

    Page<Publisher> findAllByStatus(Boolean status, Pageable pageDetails);

    Page<Publisher> findAll(Specification<Publisher> publisherSpecification, Pageable pageDetails);

}
