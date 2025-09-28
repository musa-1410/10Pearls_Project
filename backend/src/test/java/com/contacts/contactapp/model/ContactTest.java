package com.contacts.contactapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void testGettersAndSetters() {
        Contact contact = new Contact();

        contact.setId(10L);
        assertEquals(10L, contact.getId());

        contact.setFirstName("Bareeha");
        assertEquals("Bareeha", contact.getFirstName());

        contact.setLastName("Amin");
        assertEquals("Amin", contact.getLastName());

        contact.setTitle("Bareeha Amin");
        assertEquals("Bareeha Amin", contact.getTitle());

        contact.setEmail("bareeha.amin@example.com");
        assertEquals("bareeha.amin@example.com", contact.getEmail());

        contact.setEmailLabel("work");
        assertEquals("work", contact.getEmailLabel());

        contact.setPhone("0123456789");
        assertEquals("0123456789", contact.getPhone());

        contact.setPhoneLabel("mobile");
        assertEquals("mobile", contact.getPhoneLabel());

        contact.setAddress("123 Street, City");
        assertEquals("123 Street, City", contact.getAddress());
    }
}