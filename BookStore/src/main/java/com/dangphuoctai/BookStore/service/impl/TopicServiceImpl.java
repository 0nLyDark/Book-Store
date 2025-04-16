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

import com.dangphuoctai.BookStore.entity.Topic;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.TopicDTO;
import com.dangphuoctai.BookStore.payloads.response.TopicResponse;
import com.dangphuoctai.BookStore.repository.TopicRepo;
import com.dangphuoctai.BookStore.service.TopicService;
import com.dangphuoctai.BookStore.utils.CreateSlug;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepo topicRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TopicDTO getTopicById(Long topicId) {
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topicId", topicId));

        return modelMapper.map(topic, TopicDTO.class);
    }

    @Override
    public List<TopicDTO> getManyTopicById(List<Long> topicIds) {
        List<Topic> topics = topicRepo.findAllById(topicIds);
        if (topics.isEmpty()) {
            throw new ResourceNotFoundException("Topic", "topicIds", topicIds);
        }
        List<TopicDTO> topicDTOs = topics.stream()
                .map(topic -> modelMapper.map(topic, TopicDTO.class))
                .collect(Collectors.toList());
        return topicDTOs;
    }

    @Override
    public TopicDTO getTopicBySlug(String slug) {
        Topic topic = topicRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "slug", slug));

        return modelMapper.map(topic, TopicDTO.class);
    }

    @Override
    public TopicResponse getAllTopic(Boolean status, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Topic> pageTopics;
        if (status != null) {
            pageTopics = topicRepo.findAllByStatus(status,pageDetails);
        } else {
            pageTopics = topicRepo.findAll(pageDetails);
        }
        List<TopicDTO> topicDTOs = pageTopics.getContent().stream().map(topic -> modelMapper.map(topic, TopicDTO.class))
                .collect(Collectors.toList());

        TopicResponse topicResponse = new TopicResponse();
        topicResponse.setContent(topicDTOs);
        topicResponse.setPageNumber(pageTopics.getNumber());
        topicResponse.setPageSize(pageTopics.getSize());
        topicResponse.setTotalElements(pageTopics.getTotalElements());
        topicResponse.setTotalPages(pageTopics.getTotalPages());
        topicResponse.setLastPage(pageTopics.isLast());

        return topicResponse;
    }

    @Override
    public TopicDTO createTopic(TopicDTO topicDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Topic topic = new Topic();
        topic.setTopicName(topicDTO.getTopicName());
        topic.setSlug(CreateSlug.toSlug(topicDTO.getTopicName()));
        topic.setDescription(topicDTO.getDescription());
        topic.setStatus(false);
        topic.setCreatedBy(userId);
        topic.setUpdatedBy(userId);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        topicRepo.save(topic);

        return modelMapper.map(topic, TopicDTO.class);
    }

    @Override
    public TopicDTO updateTopic(TopicDTO topicDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Topic topic = topicRepo.findById(topicDTO.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topicId", topicDTO.getTopicId()));
        topic.setTopicName(topicDTO.getTopicName());
        topic.setSlug(CreateSlug.toSlug(topicDTO.getTopicName()));
        topic.setDescription(topicDTO.getDescription());
        topic.setStatus(topicDTO.getStatus());
        topic.setUpdatedBy(userId);
        topic.setUpdatedAt(LocalDateTime.now());
        topicRepo.save(topic);

        return modelMapper.map(topic, TopicDTO.class);
    }

    @Override
    public String deleteTopic(Long topicId) {
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topicId", topicId));
        topicRepo.delete(topic);

        return "Topic with ID: " + topicId + " deleted successfully";
    }
}
