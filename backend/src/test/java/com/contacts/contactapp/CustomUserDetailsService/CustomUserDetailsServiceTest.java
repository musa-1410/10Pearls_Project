package com.contacts.contactapp.CustomUserDetailsService;

import com.contacts.contactapp.Repository.userRepository;
import com.contacts.contactapp.model.user;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private userRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    public CustomUserDetailsServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_existingEmail_returnsUserDetails() {
        user mockUser = new user();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole("ROLE_USER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_nonExistingEmail_throwsException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonexistent@example.com")
        );

        assertEquals("User not found", thrown.getMessage());
    }
}