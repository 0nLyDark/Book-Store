package com.dangphuoctai.BookStore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Banner;

@Repository
public interface BannerRepo extends JpaRepository<Banner, Long> {

    Page<Banner> findAllByStatus(Boolean status, Pageable pageDetails);

}
