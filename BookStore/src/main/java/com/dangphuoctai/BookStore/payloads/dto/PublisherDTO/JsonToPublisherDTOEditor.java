package com.dangphuoctai.BookStore.payloads.dto.PublisherDTO;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import com.dangphuoctai.BookStore.exceptions.APIException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonToPublisherDTOEditor extends PropertyEditorSupport {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    @Override
    public void setAsText(String text) {
        try {
            PublisherDTO dto = objectMapper.readValue(text, PublisherDTO.class);
            setValue(dto);
        } catch (IOException e) {
            throw new APIException("Không thể chuyển JSON sang PublisherDTO" + e);
        }
    }
}
