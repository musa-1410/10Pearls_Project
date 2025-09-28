package com.contacts.contactapp.controller;

import com.contacts.contactapp.model.Contact;
import com.contacts.contactapp.Repository.ContactRepository;
import com.contacts.contactapp.config.TestConfigSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfigSecurity.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Contact testContact;

    @BeforeEach
    void setup() {
        contactRepository.deleteAll();

        testContact = new Contact();
        testContact.setFirstName("John");
        testContact.setLastName("Doe");
        testContact.setEmail("john.doe@example.com");
        testContact.setEmailLabel("Work");
        testContact.setPhone("1234567890");
        testContact.setPhoneLabel("Mobile");
        testContact.setAddress("123 Main St");
        testContact.setTitle("John Doe");
    }

    @Test
    void testCreateContact() throws Exception {
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testGetAllContacts() throws Exception {
        contactRepository.save(testContact);

        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void testGetContactById() throws Exception {
        Contact saved = contactRepository.save(testContact);

        mockMvc.perform(get("/api/contacts/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testUpdateContact() throws Exception {
        Contact saved = contactRepository.save(testContact);

        saved.setFirstName("Jane");
        saved.setLastName("Smith");
        saved.setEmail("jane.smith@example.com");

        mockMvc.perform(put("/api/contacts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }

    @Test
    void testDeleteContact() throws Exception {
        Contact saved = contactRepository.save(testContact);

        mockMvc.perform(delete("/api/contacts/{id}", saved.getId()))
                .andExpect(status().isOk());

        assertThat(contactRepository.findById(saved.getId())).isEmpty();
    }
}