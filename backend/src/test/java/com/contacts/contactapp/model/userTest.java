package com.contacts.contactapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class userTest {

    @Test
    void testAllGettersAndSetters() {
        user u = new user();

        u.setId(1L);
        assertEquals(1L, u.getId());

        u.setName("Ali");
        assertEquals("Ali", u.getName());

        u.setEmail("ali@example.com");
        assertEquals("ali@example.com", u.getEmail());

        u.setPassword("12345");
        assertEquals("12345", u.getPassword());

        u.setRole("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", u.getRole());

        u.setAdmin(true);
        assertTrue(u.getIsAdmin());
    }
}