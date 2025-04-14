package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.PublisherDTO;
import com.dangphuoctai.BookStore.payloads.dto.SupplierDTO;
import com.dangphuoctai.BookStore.payloads.response.PublisherResponse;
import com.dangphuoctai.BookStore.service.PublisherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublisherController {
    @Autowired
    private PublisherService publisherService;

    @GetMapping("/public/publishers/{publisherId}")
    public ResponseEntity<PublisherDTO> getPublisherById(@PathVariable Long publisherId) {
        PublisherDTO publisherDTO = publisherService.getPublisherById(publisherId);

        return new ResponseEntity<PublisherDTO>(publisherDTO, HttpStatus.OK);
    }

    @GetMapping("/public/publishers/ids")
    public ResponseEntity<List<PublisherDTO>> getManyPublisherByIds(
            @RequestParam(value = "id") List<Long> publisherIds) {
        List<PublisherDTO> PublisherDTOs = publisherService.getManyPublisherById(publisherIds);

        return new ResponseEntity<List<PublisherDTO>>(PublisherDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/publishers")
    public ResponseEntity<PublisherResponse> getAllPublisher(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PUBLISHERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        PublisherResponse publisherResponse = publisherService.getAllPublisher(
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "publisherId" : sortBy,
                sortOrder);

        return new ResponseEntity<PublisherResponse>(publisherResponse, HttpStatus.OK);
    }

    @PostMapping("/staff/publishers")
    public ResponseEntity<PublisherDTO> createPublisher(@RequestBody PublisherDTO publisher) {
        PublisherDTO publisherDTO = publisherService.createPublisher(publisher);

        return new ResponseEntity<PublisherDTO>(publisherDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/publishers")
    public ResponseEntity<PublisherDTO> updatePublisher(@RequestBody PublisherDTO publisher) {
        PublisherDTO publisherDTO = publisherService.updatePublisher(publisher);

        return new ResponseEntity<PublisherDTO>(publisherDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/publishers/{publisherId}")
    public ResponseEntity<String> deletePublisher(@PathVariable Long publisherId) {
        String result = publisherService.deletePublisher(publisherId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
