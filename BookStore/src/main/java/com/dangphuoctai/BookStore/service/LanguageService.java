package com.dangphuoctai.BookStore.service;

import java.io.IOException;

import com.dangphuoctai.BookStore.payloads.dto.LanguageDTO;
import com.dangphuoctai.BookStore.payloads.response.LanguageResponse;

public interface LanguageService {
    LanguageDTO getLanguageById(Long languageId);

    LanguageResponse getAllLanguages(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    LanguageDTO createLanguage(LanguageDTO languageDTO);

    LanguageDTO updateLanguage(LanguageDTO languageDTO);

    String deleteLanguage(Long languageId);
}
