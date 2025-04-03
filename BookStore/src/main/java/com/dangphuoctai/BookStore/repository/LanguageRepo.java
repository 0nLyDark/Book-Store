package com.dangphuoctai.BookStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Language;

@Repository
public interface LanguageRepo extends JpaRepository<Language, Long> {

}
