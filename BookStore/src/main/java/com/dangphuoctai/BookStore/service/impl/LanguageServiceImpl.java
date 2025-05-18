package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.entity.Language;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.LanguageDTO;
import com.dangphuoctai.BookStore.payloads.response.BannerResponse;
import com.dangphuoctai.BookStore.payloads.response.LanguageResponse;
import com.dangphuoctai.BookStore.repository.LanguageRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.LanguageService;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseRedisService<String, String, LanguageResponse> languegeResponseRedisService;

    private static final String LANGUAGE_PAGE_CACHE_KEY = "languege:pages";

    @Override
    public LanguageDTO getLanguageById(Long languageId) {
        Language language = languageRepo.findById(languageId)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "languageId", languageId));

        return modelMapper.map(language, LanguageDTO.class);
    }

    @Override
    public List<LanguageDTO> getManyLanguageById(List<Long> languageIds) {
        List<Language> languages = languageRepo.findAllById(languageIds);
        if (languages.size() != languageIds.size()) {
            throw new ResourceNotFoundException("Language", "languageIds", languageIds);
        }
        List<LanguageDTO> languageDTOs = languages.stream()
                .map(language -> modelMapper.map(language, LanguageDTO.class)).collect(Collectors.toList());

        return languageDTOs;
    }

    @Override
    public LanguageResponse getAllLanguages(Boolean status, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        String field = String.format("status:%s|page:%d|size:%d|sortBy:%s|sortOrder:%s",
                status, pageNumber, pageSize, sortBy, sortOrder);
        LanguageResponse cached = (LanguageResponse) languegeResponseRedisService.hashGet(LANGUAGE_PAGE_CACHE_KEY,
                field);
        if (cached != null) {
            return cached;
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Language> pageLanguages;
        if (status != null) {
            pageLanguages = languageRepo.findAllByStatus(status, pageDetails);
        } else {
            pageLanguages = languageRepo.findAll(pageDetails);
        }
        List<LanguageDTO> languageDTOs = pageLanguages.getContent().stream()
                .map(language -> modelMapper.map(language, LanguageDTO.class))
                .collect(Collectors.toList());

        LanguageResponse languageResponse = new LanguageResponse();
        languageResponse.setContent(languageDTOs);
        languageResponse.setPageNumber(pageLanguages.getNumber());
        languageResponse.setPageSize(pageLanguages.getSize());
        languageResponse.setTotalElements(pageLanguages.getTotalElements());
        languageResponse.setTotalPages(pageLanguages.getTotalPages());
        languageResponse.setLastPage(pageLanguages.isLast());

        // Save cache banner to redis
        languegeResponseRedisService.hashSet(LANGUAGE_PAGE_CACHE_KEY, field, languageResponse);
        languegeResponseRedisService.setTimeToLiveOnce(LANGUAGE_PAGE_CACHE_KEY, 3, TimeUnit.HOURS);

        return languageResponse;
    }

    @Override
    public LanguageDTO createLanguage(LanguageDTO languageDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Language language = new Language();
        language.setName(languageDTO.getName());
        language.setStatus(false);

        language.setCreatedBy(userId);
        language.setUpdatedBy(userId);
        language.setCreatedAt(LocalDateTime.now());
        language.setUpdatedAt(LocalDateTime.now());
        languageRepo.save(language);

        // Save cache banner to redis
        languegeResponseRedisService.delete(LANGUAGE_PAGE_CACHE_KEY);

        return modelMapper.map(language, LanguageDTO.class);
    }

    @Override
    public LanguageDTO updateLanguage(LanguageDTO languageDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Language language = languageRepo.findById(languageDTO.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language", "languageId",
                        languageDTO.getLanguageId()));
        language.setName(languageDTO.getName());
        language.setStatus(languageDTO.getStatus());

        language.setUpdatedBy(userId);
        language.setUpdatedAt(LocalDateTime.now());
        languageRepo.save(language);
        // Save cache banner to redis
        languegeResponseRedisService.delete(LANGUAGE_PAGE_CACHE_KEY);

        return modelMapper.map(language, LanguageDTO.class);
    }

    @Override
    public String deleteLanguage(Long languageId) {
        Language language = languageRepo.findById(languageId)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "languageId", languageId));
        languageRepo.delete(language);

        // Save cache banner to redis
        languegeResponseRedisService.delete(LANGUAGE_PAGE_CACHE_KEY);

        return "Language with ID: " + languageId + " deleted successfully";
    }

}
