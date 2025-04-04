package com.dangphuoctai.BookStore.controller;

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
import com.dangphuoctai.BookStore.payloads.dto.ContactDTO;
import com.dangphuoctai.BookStore.payloads.response.ContactResponse;
import com.dangphuoctai.BookStore.service.ContactService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContactController {
    @Autowired
    private ContactService contactService;

    @GetMapping("/staff/contacts/{contactId}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long contactId) {
        ContactDTO contactDTO = contactService.getContactById(contactId);

        return new ResponseEntity<ContactDTO>(contactDTO, HttpStatus.OK);
    }

    @GetMapping("/staff/contacts")
    public ResponseEntity<ContactResponse> getAllContacts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CONTACTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        ContactResponse contactResponse = contactService.getAllContacts(
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "contactId" : sortBy,
                sortOrder);

        return new ResponseEntity<ContactResponse>(contactResponse, HttpStatus.OK);
    }

    @PostMapping("/public/contacts")
    public ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO contact) {
        ContactDTO contactDTO = contactService.createContact(contact);

        return new ResponseEntity<ContactDTO>(contactDTO, HttpStatus.CREATED);
    }

    @PutMapping("/staff/contacts")
    public ResponseEntity<ContactDTO> updateContact(@RequestBody ContactDTO contact) {
        ContactDTO contactDTO = contactService.updateContactIsRead(contact);

        return new ResponseEntity<ContactDTO>(contactDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/contacts/{contactId}")
    public ResponseEntity<String> deleteContact(@PathVariable Long contactId) {
        String result = contactService.deleteContact(contactId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
