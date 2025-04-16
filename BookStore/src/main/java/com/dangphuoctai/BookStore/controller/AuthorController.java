package com.dangphuoctai.BookStore.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.AuthorDTO;
import com.dangphuoctai.BookStore.payloads.response.AuthorResponse;
import com.dangphuoctai.BookStore.service.AuthorService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/public/authors/{authorId}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long authorId) {
        AuthorDTO authorDTO = authorService.getAuthorById(authorId);

        return new ResponseEntity<AuthorDTO>(authorDTO, HttpStatus.OK);
    }

    @GetMapping("/public/authors/ids")
    public ResponseEntity<List<AuthorDTO>> getManyAuthorByIds(@RequestParam(value = "id") List<Long> authorIds) {
        List<AuthorDTO> authorDTOs = authorService.getManyAuthorById(authorIds);

        return new ResponseEntity<List<AuthorDTO>>(authorDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/authors")
    public ResponseEntity<AuthorResponse> getAllAuthor(
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_AUTHORS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        AuthorResponse authorResponse = authorService.getAllAuthors(status,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "authorId" : sortBy,
                sortOrder);

        return new ResponseEntity<AuthorResponse>(authorResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/authors")
    public ResponseEntity<AuthorDTO> createAuthor(@RequestParam(value = "file", required = false) MultipartFile image,
            @ModelAttribute AuthorDTO author) throws IOException {
        System.out.println("create author: " + author);
        AuthorDTO authorDTO = authorService.createAuthor(author, image);

        return new ResponseEntity<AuthorDTO>(authorDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/authors")
    public ResponseEntity<AuthorDTO> updateAuthor(@RequestParam(value = "file", required = false) MultipartFile image,
            @ModelAttribute AuthorDTO author) throws IOException {
        AuthorDTO authorDTO = authorService.updateAuthor(author, image);

        return new ResponseEntity<AuthorDTO>(authorDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/authors/{authorId}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long authorId) {
        String result = authorService.deleteAuthor(authorId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

}
