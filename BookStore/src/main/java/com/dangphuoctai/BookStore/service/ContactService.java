package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.EmailDetails;
import com.dangphuoctai.BookStore.payloads.dto.ContactDTO;
import com.dangphuoctai.BookStore.payloads.response.ContactResponse;

public interface ContactService {

    ContactDTO getContactById(Long contactId);

    ContactResponse getAllContacts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ContactDTO createContact(ContactDTO contactDTO);

    ContactDTO updateContactIsRead(ContactDTO contactDTO);

    String deleteContact(Long contactId);

    String sendEmailContact(Long contactId, EmailDetails emailDetails);

}
