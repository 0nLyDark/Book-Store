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

import com.dangphuoctai.BookStore.entity.Publisher;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.PublisherDTO;
import com.dangphuoctai.BookStore.payloads.response.PublisherResponse;
import com.dangphuoctai.BookStore.repository.PublisherRepo;
import com.dangphuoctai.BookStore.service.PublisherService;
import com.dangphuoctai.BookStore.utils.CreateSlug;

@Service
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    private PublisherRepo publisherRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PublisherDTO getPublisherById(Long publisherId) {
        Publisher publisher = publisherRepo.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "publisherId", publisherId));

        return modelMapper.map(publisher, PublisherDTO.class);
    }

    @Override
    public PublisherResponse getAllPublisher(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Publisher> pagePublishers = publisherRepo.findAll(pageDetails);
        List<PublisherDTO> publisherDTOs = pagePublishers.getContent().stream()
                .map(publisher -> modelMapper.map(publisher, PublisherDTO.class))
                .collect(Collectors.toList());

        PublisherResponse publisherResponse = new PublisherResponse();
        publisherResponse.setContent(publisherDTOs);
        publisherResponse.setPageNumber(pagePublishers.getNumber());
        publisherResponse.setPageSize(pagePublishers.getSize());
        publisherResponse.setTotalElements(pagePublishers.getTotalElements());
        publisherResponse.setTotalPages(pagePublishers.getTotalPages());
        publisherResponse.setLastPage(pagePublishers.isLast());

        return publisherResponse;
    }

    @Override
    public PublisherDTO createPublisher(PublisherDTO publisherDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Publisher publisher = new Publisher();
        publisher.setPublisherName(publisherDTO.getPublisherName());
        publisher.setStatus(false);

        publisher.setCreatedBy(userId);
        publisher.setUpdatedBy(userId);
        publisher.setCreatedAt(LocalDateTime.now());
        publisher.setUpdatedAt(LocalDateTime.now());
        publisherRepo.save(publisher);

        return modelMapper.map(publisher, PublisherDTO.class);
    }

    @Override
    public PublisherDTO updatePublisher(PublisherDTO publisherDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Publisher publisher = publisherRepo.findById(publisherDTO.getPublisherId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Publisher", "publisherId", publisherDTO.getPublisherId()));
        publisher.setPublisherName(publisherDTO.getPublisherName());
        publisher.setStatus(publisherDTO.getStatus());
        
        publisher.setUpdatedBy(userId);
        publisher.setUpdatedAt(LocalDateTime.now());
        publisherRepo.save(publisher);

        return modelMapper.map(publisher, PublisherDTO.class);
    }

    @Override
    public String deletePublisher(Long publisherId) {
        Publisher publisher = publisherRepo.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "publisherId", publisherId));
        publisherRepo.delete(publisher);

        return "Publisher with ID: " + publisherId + " deleted successfully";
    }

}
