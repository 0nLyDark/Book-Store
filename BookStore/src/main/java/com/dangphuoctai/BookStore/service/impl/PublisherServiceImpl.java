package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.entity.Publisher;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.Specification.PublisherSpecification;
import com.dangphuoctai.BookStore.payloads.dto.AuthorDTO;
import com.dangphuoctai.BookStore.payloads.dto.PromotionDTO;
import com.dangphuoctai.BookStore.payloads.dto.PublisherDTO.PublisherDTO;
import com.dangphuoctai.BookStore.payloads.response.PromotionResponse;
import com.dangphuoctai.BookStore.payloads.response.PublisherResponse;
import com.dangphuoctai.BookStore.repository.PublisherRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.service.PublisherService;

@Service
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    private PublisherRepo publisherRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    private BaseRedisService<String, String, PublisherDTO> publisherRedisService;

    @Autowired
    private BaseRedisService<String, String, PublisherResponse> publisherResponseRedisService;

    private static final String PUBLISHER_CACHE_KEY = "publisher";
    private static final String PUBLISHER_PAGE_CACHE_KEY = "publisher:pages";

    @Override
    public PublisherDTO getPublisherById(Long publisherId) {
        String field = "id:" + publisherId;
        PublisherDTO cached = (PublisherDTO) publisherRedisService.hashGet(PUBLISHER_CACHE_KEY, field);
        if (cached != null) {
            return cached;
        }
        Publisher publisher = publisherRepo.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "publisherId", publisherId));

        PublisherDTO publisherDTO = modelMapper.map(publisher, PublisherDTO.class);

        publisherRedisService.hashSet(PUBLISHER_CACHE_KEY, field, publisherDTO);
        publisherRedisService.setTimeToLiveOnce(PUBLISHER_CACHE_KEY, 3, TimeUnit.HOURS);

        return publisherDTO;
    }

    @Override
    public List<PublisherDTO> getManyPublisherById(List<Long> publisherIds) {
        List<Publisher> publishers = publisherRepo.findAllById(publisherIds);
        if (publishers.size() != publisherIds.size()) {
            throw new ResourceNotFoundException("Publisher", "publisherIds", publisherIds);
        }
        List<PublisherDTO> publisherDTOs = publishers.stream()
                .map(publisher -> modelMapper.map(publisher, PublisherDTO.class)).collect(Collectors.toList());

        return publisherDTOs;
    }

    @Override
    public PublisherResponse getAllPublisher(String keyword, Boolean status, Integer pageNumber, Integer pageSize,
            String sortBy,
            String sortOrder) {
        String field = String.format("keyword:%s|status:%s|page:%d|size:%d|sortBy:%s|sortOrder:%s", keyword,
                status, pageNumber, pageSize, sortBy, sortOrder);
        PublisherResponse cached = (PublisherResponse) publisherResponseRedisService.hashGet(PUBLISHER_PAGE_CACHE_KEY,
                field);
        if (cached != null) {
            return cached;
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Publisher> publisherSpecification = PublisherSpecification.filter(keyword, status);

        Page<Publisher> pagePublishers = publisherRepo.findAll(publisherSpecification, pageDetails);

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

        // Save cache publisher to redis
        publisherResponseRedisService.hashSet(PUBLISHER_PAGE_CACHE_KEY, field, publisherResponse);
        publisherResponseRedisService.setTimeToLive(PUBLISHER_PAGE_CACHE_KEY, 3, TimeUnit.HOURS);

        return publisherResponse;
    }

    @Transactional
    @Override
    public PublisherDTO createPublisher(PublisherDTO publisherDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Publisher publisher = new Publisher();
        publisher.setPublisherName(publisherDTO.getPublisherName());
        if (image != null) {
            String fileName = fileService.uploadImage(path, image);
            publisher.setImage(fileName);
        }
        publisher.setStatus(false);

        publisher.setCreatedBy(userId);
        publisher.setUpdatedBy(userId);
        publisher.setCreatedAt(LocalDateTime.now());
        publisher.setUpdatedAt(LocalDateTime.now());
        publisherRepo.save(publisher);
        PublisherDTO publisherRes = modelMapper.map(publisher, PublisherDTO.class);
        // Save cache publisher to redis
        String field = "id:" + publisher.getPublisherId();
        publisherRedisService.hashSet(PUBLISHER_CACHE_KEY, field, publisherRes);
        publisherRedisService.setTimeToLive(PUBLISHER_CACHE_KEY, 3, TimeUnit.HOURS);
        publisherResponseRedisService.delete(PUBLISHER_PAGE_CACHE_KEY);

        return publisherRes;
    }

    @Transactional
    @Override
    public PublisherDTO updatePublisher(PublisherDTO publisherDTO, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Publisher publisher = publisherRepo.findById(publisherDTO.getPublisherId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Publisher", "publisherId", publisherDTO.getPublisherId()));
        publisher.setPublisherName(publisherDTO.getPublisherName());
        if (image != null) {
            String fileName = fileService.uploadImage(path, image);
            publisher.setImage(fileName);
        }
        publisher.setStatus(publisherDTO.getStatus());

        publisher.setUpdatedBy(userId);
        publisher.setUpdatedAt(LocalDateTime.now());
        publisherRepo.save(publisher);

        PublisherDTO publisherRes = modelMapper.map(publisher, PublisherDTO.class);
        // Save cache publisher to redis
        String field = "id:" + publisher.getPublisherId();
        publisherRedisService.hashSet(PUBLISHER_CACHE_KEY, field, publisherRes);
        publisherRedisService.setTimeToLive(PUBLISHER_CACHE_KEY, 3, TimeUnit.HOURS);
        publisherResponseRedisService.delete(PUBLISHER_PAGE_CACHE_KEY);

        return publisherRes;
    }

    @Override
    public String deletePublisher(Long publisherId) {
        Publisher publisher = publisherRepo.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "publisherId", publisherId));
        publisherRepo.delete(publisher);

        // Save cache publisher to redis
        String field = "id:" + publisherId;
        publisherRedisService.delete(PUBLISHER_CACHE_KEY, field);
        publisherResponseRedisService.delete(PUBLISHER_PAGE_CACHE_KEY);

        return "Nhà xuất bản với ID: " + publisherId + " đã được xóa thành công";
    }

}
