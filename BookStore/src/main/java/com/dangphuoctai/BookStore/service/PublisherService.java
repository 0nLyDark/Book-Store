package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.dto.PublisherDTO;
import com.dangphuoctai.BookStore.payloads.response.PublisherResponse;

public interface PublisherService {
    PublisherDTO getPublisherById(Long publisherId);

    PublisherResponse getAllPublisher(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    PublisherDTO createPublisher(PublisherDTO PublisherDTO);

    PublisherDTO updatePublisher(PublisherDTO PublisherDTO);

    String deletePublisher(Long publisherId);
}
