package com.dangphuoctai.BookStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.TopicDTO;
import com.dangphuoctai.BookStore.payloads.response.TopicResponse;
import com.dangphuoctai.BookStore.service.TopicService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping("/public/topics/{topicId}")
    public ResponseEntity<TopicDTO> getTopicById(@PathVariable Long topicId) {
        TopicDTO topicDTO = topicService.getTopicById(topicId);

        return new ResponseEntity<TopicDTO>(topicDTO, HttpStatus.OK);
    }

    @GetMapping("/public/topics/slug/{slug}")
    public ResponseEntity<TopicDTO> getTopicBySlug(@PathVariable String slug) {
        TopicDTO topicDTO = topicService.getTopicBySlug(slug);

        return new ResponseEntity<TopicDTO>(topicDTO, HttpStatus.OK);
    }

    @GetMapping("/public/topics")
    public ResponseEntity<TopicResponse> getAllTopic(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_TOPICS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        TopicResponse topicResponse = topicService.getAllTopic(
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "topicId" : sortBy,
                sortOrder);

        return new ResponseEntity<TopicResponse>(topicResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/topics")
    public ResponseEntity<TopicDTO> createTopic(@RequestBody TopicDTO topic) {
        TopicDTO topicDTO = topicService.createTopic(topic);

        return new ResponseEntity<TopicDTO>(topicDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/topics")
    public ResponseEntity<TopicDTO> updateTopic(@RequestBody TopicDTO topic) {
        TopicDTO topicDTO = topicService.updateTopic(topic);

        return new ResponseEntity<TopicDTO>(topicDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/topics/{topicId}")
    public ResponseEntity<String> deleteTopic(@PathVariable Long topicId) {
        String result = topicService.deleteTopic(topicId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
