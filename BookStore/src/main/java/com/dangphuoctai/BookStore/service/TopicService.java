package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.TopicDTO;
import com.dangphuoctai.BookStore.payloads.response.TopicResponse;

public interface TopicService {
    TopicDTO getTopicById(Long topicId);

    List<TopicDTO> getManyTopicById(List<Long> topicIds);

    TopicDTO getTopicBySlug(String slug);

    TopicResponse getAllTopic(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    TopicDTO createTopic(TopicDTO topicDTO);

    TopicDTO updateTopic(TopicDTO topicDTO);

    String deleteTopic(Long topicId);
}
