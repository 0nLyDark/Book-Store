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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.entity.Contact;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.EmailDetails;
import com.dangphuoctai.BookStore.payloads.dto.ContactDTO;
import com.dangphuoctai.BookStore.payloads.response.ContactResponse;
import com.dangphuoctai.BookStore.repository.ContactRepo;
import com.dangphuoctai.BookStore.service.ContactService;
import com.dangphuoctai.BookStore.service.EmailService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Override
    public ContactDTO getContactById(Long contactId) {
        Contact contact = contactRepo.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "contactId", contactId));
        contact.setIsRead(true);
        contactRepo.save(contact);

        return modelMapper.map(contact, ContactDTO.class);
    }

    @Override
    public ContactResponse getAllContacts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Contact> pageContacts = contactRepo.findAll(pageDetails);
        List<ContactDTO> contactDTOs = pageContacts.getContent().stream()
                .map(contact -> modelMapper.map(contact, ContactDTO.class))
                .collect(Collectors.toList());

        ContactResponse contactResponse = new ContactResponse();
        contactResponse.setContent(contactDTOs);
        contactResponse.setPageNumber(pageContacts.getNumber());
        contactResponse.setPageSize(pageContacts.getSize());
        contactResponse.setTotalElements(pageContacts.getTotalElements());
        contactResponse.setTotalPages(pageContacts.getTotalPages());
        contactResponse.setLastPage(pageContacts.isLast());

        return contactResponse;
    }

    @Override
    public ContactDTO createContact(ContactDTO contactDTO) {
        Contact contact = new Contact();
        contact.setEmail(contactDTO.getEmail());
        contact.setMobileNumber(contactDTO.getMobileNumber());
        contact.setTitle(contactDTO.getTitle());
        contact.setContent(contactDTO.getContent());
        contact.setIsRead(false);

        contact.setCreatedAt(LocalDateTime.now());
        contactRepo.save(contact);

        return modelMapper.map(contact, ContactDTO.class);
    }

    @Override
    public ContactDTO updateContactIsRead(ContactDTO contactDTO) {
        Contact contact = contactRepo.findById(contactDTO.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "contactId",
                        contactDTO.getContactId()));
        contact.setIsRead(contactDTO.getIsRead());

        contactRepo.save(contact);

        return modelMapper.map(contact, ContactDTO.class);
    }

    @Override
    public String deleteContact(Long contactId) {
        Contact contact = contactRepo.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "contactId", contactId));
        contactRepo.delete(contact);

        return "Contact with ID: " + contactId + " deleted successfully";
    }

    @Transactional
    @Override
    public String sendEmailContact(Long contactId, EmailDetails emailDetails) {
        Contact contact = contactRepo.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "contactId", contactId));
        contact.setIsRely(true);
        contactRepo.save(contact);

        emailDetails.setRecipient(contact.getEmail());
        String result = emailService.sendMailWithAttachment(emailDetails);

        return result;
    }

}
