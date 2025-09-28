package com.contacts.contactapp.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PasswordChangeRequestTest {

    @Test
    void testGettersAndSetters() {
        PasswordChangeRequest request = new PasswordChangeRequest();

        request.setEmail("user@example.com");
        request.setCurrentPassword("oldPass123");
        request.setNewPassword("newPass456");

        assertEquals("user@example.com", request.getEmail());
        assertEquals("oldPass123", request.getCurrentPassword());
        assertEquals("newPass456", request.getNewPassword());
    }
}