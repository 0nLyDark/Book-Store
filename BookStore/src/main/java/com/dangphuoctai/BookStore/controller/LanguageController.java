package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.LanguageDTO;
import com.dangphuoctai.BookStore.payloads.response.LanguageResponse;
import com.dangphuoctai.BookStore.service.LanguageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LanguageController {
    @Autowired
    private LanguageService languageService;

    @GetMapping("/public/languages/{languageId}")
    public ResponseEntity<LanguageDTO> getLanguageById(@PathVariable Long languageId) {
        LanguageDTO languageDTO = languageService.getLanguageById(languageId);

        return new ResponseEntity<LanguageDTO>(languageDTO, HttpStatus.OK);
    }

    @GetMapping("/public/languages/ids")
    public ResponseEntity<List<LanguageDTO>> getManyLanguageByIds(
            @RequestParam(value = "id") List<Long> languageIds) {
        List<LanguageDTO> LanguageDTOs = languageService.getManyLanguageById(languageIds);

        return new ResponseEntity<List<LanguageDTO>>(LanguageDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/languages")
    public ResponseEntity<LanguageResponse> getAllLanguages(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_LANGUAGES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        LanguageResponse languageResponse = languageService.getAllLanguages(
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "languageId" : sortBy,
                sortOrder);

        return new ResponseEntity<LanguageResponse>(languageResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/languages")
    public ResponseEntity<LanguageDTO> createLanguage(@RequestBody LanguageDTO language) {
        LanguageDTO languageDTO = languageService.createLanguage(language);

        return new ResponseEntity<LanguageDTO>(languageDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/languages")
    public ResponseEntity<LanguageDTO> updateLanguage(@RequestBody LanguageDTO language) {
        LanguageDTO languageDTO = languageService.updateLanguage(language);

        return new ResponseEntity<LanguageDTO>(languageDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/languages/{languageId}")
    public ResponseEntity<String> deleteLanguage(@PathVariable Long languageId) {
        String result = languageService.deleteLanguage(languageId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
