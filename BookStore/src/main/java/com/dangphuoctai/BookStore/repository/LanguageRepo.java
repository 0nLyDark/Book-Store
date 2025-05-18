package com.dangphuoctai.BookStore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Language;

@Repository
public interface LanguageRepo extends JpaRepository<Language, Long> {

    Page<Language> findAllByStatus(Boolean status, Pageable pageDetails);

}
