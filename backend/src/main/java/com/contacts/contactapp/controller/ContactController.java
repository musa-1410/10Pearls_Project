package com.contacts.contactapp.controller;

import com.contacts.contactapp.model.Contact;
import com.contacts.contactapp.Repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactRepository repository;

    @GetMapping
    public List<Contact> getAll() {
        logger.info("[CONTACTS] Fetching all contacts...");
        List<Contact> contacts = repository.findAll();
        logger.info("[CONTACTS] Total contacts fetched: {}", contacts.size());
        return contacts;
    }

    @PostMapping
    public Contact create(@RequestBody Contact contact) {
        // Set title by concatenating firstName and lastName
        contact.setTitle(contact.getFirstName() + " " + contact.getLastName());

        Contact savedContact = repository.save(contact);
        logger.info("[ADD] Contact added: ID={}, Title={}, Email={} [{}], Phone={} [{}]",
                savedContact.getId(),
                savedContact.getTitle(),
                savedContact.getEmail(),
                savedContact.getEmailLabel(),
                savedContact.getPhone(),
                savedContact.getPhoneLabel());
        return savedContact;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> update(@PathVariable Long id, @RequestBody Contact updated) {
        return repository.findById(id)
                .map(existing -> {
                    logger.info("[EDIT] Updating contact ID {}: Old Title={}, New Title={}",
                            id,
                            existing.getTitle(),
                            updated.getFirstName() + " " + updated.getLastName());

                    existing.setFirstName(updated.getFirstName());
                    existing.setLastName(updated.getLastName());
                    // Automatically set new title
                    existing.setTitle(updated.getFirstName() + " " + updated.getLastName());

                    existing.setEmail(updated.getEmail());
                    existing.setEmailLabel(updated.getEmailLabel());

                    existing.setPhone(updated.getPhone());
                    existing.setPhoneLabel(updated.getPhoneLabel());

                    existing.setAddress(updated.getAddress());

                    Contact updatedContact = repository.save(existing);
                    logger.info("[EDIT] Contact updated successfully: ID={}, Title={}",
                            updatedContact.getId(), updatedContact.getTitle());
                    return ResponseEntity.ok(updatedContact);
                })
                .orElseGet(() -> {
                    logger.warn("[EDIT] Contact not found with ID={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
        logger.info("[DELETE] Contact deleted: ID={}", id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("[GET] Contact not found with ID={}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
