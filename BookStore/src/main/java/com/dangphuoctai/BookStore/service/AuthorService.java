package com.dangphuoctai.BookStore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.AuthorDTO;
import com.dangphuoctai.BookStore.payloads.response.AuthorResponse;

public interface AuthorService {

    AuthorDTO getAuthorById(Long AuthorId);

    List<AuthorDTO> getManyAuthorById(List<Long> authorIds);

    AuthorResponse getAllAuthors(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AuthorDTO createAuthor(AuthorDTO AuthorDTO, MultipartFile image) throws IOException;

    AuthorDTO updateAuthor(AuthorDTO AuthorDTO, MultipartFile image) throws IOException;

    String deleteAuthor(Long AuthorId);
}
