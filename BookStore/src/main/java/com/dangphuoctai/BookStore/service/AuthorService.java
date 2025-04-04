package com.dangphuoctai.BookStore.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.AuthorDTO;
import com.dangphuoctai.BookStore.payloads.response.AuthorResponse;

public interface AuthorService {

    AuthorDTO getAuthorById(Long AuthorId);

    AuthorResponse getAllAuthors(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AuthorDTO createAuthor(AuthorDTO AuthorDTO, MultipartFile image) throws IOException;

    AuthorDTO updateAuthor(AuthorDTO AuthorDTO, MultipartFile image) throws IOException;

    String deleteAuthor(Long AuthorId);
}
