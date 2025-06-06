package com.dangphuoctai.BookStore.payloads.dto.PostDTO;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.payloads.dto.TopicDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonToTopicDTOEditor extends PropertyEditorSupport {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    @Override
    public void setAsText(String text) {
        try {
            TopicDTO dto = objectMapper.readValue(text, TopicDTO.class);
            setValue(dto);
        } catch (IOException e) {
            throw new APIException("Không thể chuyển JSON sang TopicDTO" + e);
        }
    }
}
