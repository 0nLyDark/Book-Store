package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.LanguageDTO;
import com.dangphuoctai.BookStore.payloads.response.LanguageResponse;

public interface LanguageService {
    LanguageDTO getLanguageById(Long languageId);

    List<LanguageDTO> getManyLanguageById(List<Long> languageIds);

    LanguageResponse getAllLanguages(String keyword,Boolean status, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    LanguageDTO createLanguage(LanguageDTO languageDTO);

    LanguageDTO updateLanguage(LanguageDTO languageDTO);

    String deleteLanguage(Long languageId);
}
