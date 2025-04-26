package com.dangphuoctai.BookStore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.PublisherDTO.PublisherDTO;
import com.dangphuoctai.BookStore.payloads.response.PublisherResponse;

public interface PublisherService {
    PublisherDTO getPublisherById(Long publisherId);

    List<PublisherDTO> getManyPublisherById(List<Long> publisherIds);

    PublisherResponse getAllPublisher(Boolean status, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    PublisherDTO createPublisher(PublisherDTO PublisherDTO, MultipartFile image) throws IOException;

    PublisherDTO updatePublisher(PublisherDTO PublisherDTO, MultipartFile image) throws IOException;

    String deletePublisher(Long publisherId);
}
