package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.entity.Author;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.AuthorDTO;
import com.dangphuoctai.BookStore.payloads.response.AuthorResponse;
import com.dangphuoctai.BookStore.repository.AuthorRepo;
import com.dangphuoctai.BookStore.service.AuthorService;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.FileService;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    private BaseRedisService<String, String, AuthorDTO> authorRedisService;

    @Autowired
    private BaseRedisService<String, String, AuthorResponse> authorResponseRedisService;

    private static final String AUTHOR_CACHE_KEY = "author";
    private static final String AUTHOR_PAGE_CACHE_KEY = "author:pages";

    @Override
    public AuthorDTO getAuthorById(Long authorId) {
        String field = "id:" + authorId;
        AuthorDTO cached = (AuthorDTO) authorRedisService.hashGet(AUTHOR_CACHE_KEY, field);
        if (cached != null) {
            return cached;
        }
        Author author = authorRepo.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "authorId", authorId));
        AuthorDTO authorDTO = modelMapper.map(author, AuthorDTO.class);
        // Save cache author to redis
        authorRedisService.hashSet(AUTHOR_CACHE_KEY, field, authorDTO);
        authorRedisService.setTimeToLiveOnce(AUTHOR_CACHE_KEY, 3, TimeUnit.HOURS);

        return authorDTO;
    }

    @Override
    public List<AuthorDTO> getManyAuthorById(List<Long> authorIds) {
        List<Author> authors = authorRepo.findAllById(authorIds);
        if (authors.size() != authorIds.size()) {
            throw new ResourceNotFoundException("Author", "authorIds", authorIds);
        }
        List<AuthorDTO> authorDTOs = authors.stream()
                .map(author -> modelMapper.map(author, AuthorDTO.class)).collect(Collectors.toList());

        return authorDTOs;
    }

    @Override
    public AuthorResponse getAllAuthors(Boolean status, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        String field = String.format("status:%s|page:%d|size:%d|sortBy:%s|sortOrder:%s",
                status, pageNumber, pageSize, sortBy, sortOrder);
        AuthorResponse cached = (AuthorResponse) authorResponseRedisService.hashGet(AUTHOR_PAGE_CACHE_KEY, field);
        if (cached != null) {
            return cached;
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Author> pageAuthors;
        if (status == null) {
            pageAuthors = authorRepo.findAll(pageDetails);
        } else {
            pageAuthors = authorRepo.findAllByStatus(status, pageDetails);
        }

        List<AuthorDTO> authorDTOs = pageAuthors.getContent().stream()
                .map(author -> modelMapper.map(author, AuthorDTO.class))
                .collect(Collectors.toList());

        AuthorResponse authorResponse = new AuthorResponse();
        authorResponse.setContent(authorDTOs);
        authorResponse.setPageNumber(pageAuthors.getNumber());
        authorResponse.setPageSize(pageAuthors.getSize());
        authorResponse.setTotalElements(pageAuthors.getTotalElements());
        authorResponse.setTotalPages(pageAuthors.getTotalPages());
        authorResponse.setLastPage(pageAuthors.isLast());

        // Save cache author to redis
        authorResponseRedisService.hashSet(AUTHOR_PAGE_CACHE_KEY, field, authorResponse);
        authorResponseRedisService.setTimeToLive(AUTHOR_PAGE_CACHE_KEY, 3, TimeUnit.HOURS);

        return authorResponse;
    }

    @Transactional
    @Override
    public AuthorDTO createAuthor(AuthorDTO authorDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Author author = new Author();
        author.setAuthorName(authorDTO.getAuthorName());
        author.setDescription(authorDTO.getDescription());

        if (image != null) {
            String fileName = fileService.uploadImage(path, image);
            author.setImage(fileName);
        }
        author.setStatus(false);

        author.setCreatedBy(userId);
        author.setUpdatedBy(userId);
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());
        authorRepo.save(author);
        AuthorDTO authorRes = modelMapper.map(author, AuthorDTO.class);
        // Save cache author to redis
        String field = "id:" + authorRes.getAuthorId();
        authorRedisService.hashSet(AUTHOR_CACHE_KEY, field, authorRes);
        authorRedisService.setTimeToLive(AUTHOR_CACHE_KEY, 3, TimeUnit.HOURS);
        authorResponseRedisService.delete(AUTHOR_PAGE_CACHE_KEY);

        return authorRes;
    }

    @Transactional
    @Override
    public AuthorDTO updateAuthor(AuthorDTO authorDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Author author = authorRepo.findById(authorDTO.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", "authorId", authorDTO.getAuthorId()));
        author.setAuthorName(authorDTO.getAuthorName());
        author.setAuthorName(authorDTO.getAuthorName());
        author.setDescription(authorDTO.getDescription());
        if (image != null) {
            String fileName = fileService.uploadImage(path, image);
            author.setImage(fileName);
        }
        author.setStatus(authorDTO.getStatus());

        author.setUpdatedBy(userId);
        author.setUpdatedAt(LocalDateTime.now());
        authorRepo.save(author);
        AuthorDTO authorRes = modelMapper.map(author, AuthorDTO.class);
        // Save cache author to redis
        String field = "id:" + authorRes.getAuthorId();
        authorRedisService.hashSet(AUTHOR_CACHE_KEY, field, authorRes);
        authorRedisService.setTimeToLive(AUTHOR_CACHE_KEY, 3, TimeUnit.HOURS);
        authorResponseRedisService.delete(AUTHOR_PAGE_CACHE_KEY);

        return authorRes;
    }

    @Override
    public String deleteAuthor(Long authorId) {
        Author author = authorRepo.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "authorId", authorId));
        authorRepo.delete(author);
        // Save cache author to redis
        String field = "id:" + authorId;
        authorRedisService.delete(AUTHOR_CACHE_KEY, field);
        authorResponseRedisService.delete(AUTHOR_PAGE_CACHE_KEY);

        return "Author with ID: " + authorId + " deleted successfully";
    }
}
