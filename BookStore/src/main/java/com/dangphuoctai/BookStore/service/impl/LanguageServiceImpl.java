package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
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
import com.dangphuoctai.BookStore.payloads.response.LanguageResponse;
import com.dangphuoctai.BookStore.repository.LanguageRepo;
import com.dangphuoctai.BookStore.service.LanguageService;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LanguageDTO getLanguageById(Long languageId) {
        Language language = languageRepo.findById(languageId)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "languageId", languageId));

        return modelMapper.map(language, LanguageDTO.class);
    }

    @Override
    public LanguageResponse getAllLanguages(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Language> pageLanguages = languageRepo.findAll(pageDetails);
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

        return modelMapper.map(language, LanguageDTO.class);
    }

    @Override
    public String deleteLanguage(Long languageId) {
        Language language = languageRepo.findById(languageId)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "languageId", languageId));
        languageRepo.delete(language);

        return "Language with ID: " + languageId + " deleted successfully";
    }

}
