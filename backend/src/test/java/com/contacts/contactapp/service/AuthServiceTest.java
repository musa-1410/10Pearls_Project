package com.contacts.contactapp.service;

import com.contacts.contactapp.JwtUtil.JwtUtil;
import com.contacts.contactapp.Repository.userRepository;
import com.contacts.contactapp.emailservice.EmailService;
import com.contacts.contactapp.model.user;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private userRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private user testUser;

    @BeforeEach
    void setUp() {
        testUser = new user();
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPass");
        testUser.setAdmin(false);

    }

    // ---------- registerUser ----------
    @Test
    void registerUser_whenEmailExists_returnsErrorMessage() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        String result = authService.registerUser(testUser);

        assertEquals("Email already exists.", result);
        verify(userRepository, never()).save(any(user.class));
    }

    @Test
    void registerUser_whenNewUser_savesAndReturnsSuccess() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        String result = authService.registerUser(testUser);

        assertEquals("User registered successfully.", result);
        verify(userRepository).save(any(user.class));
    }

    // ---------- handleForgotPassword ----------
    @Test
    void handleForgotPassword_whenUserNotFound_returnsErrorMessage() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        String result = authService.handleForgotPassword("test@example.com");

        assertEquals("User not found with this email.", result);
        verify(emailService, never()).sendResetEmail(anyString(), anyString());
    }

    @Test
    void handleForgotPassword_whenUserFound_sendsEmailAndReturnsSuccess() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        String result = authService.handleForgotPassword("test@example.com");

        assertEquals("Password reset link has been sent to your email.", result);
        verify(emailService).sendResetEmail(eq("test@example.com"), contains("reset-password"));
    }

    // ---------- resetPassword ----------
    @Test
    void resetPassword_whenUserNotFound_returnsError() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        String result = authService.resetPassword("test@example.com", "newPass");

        assertEquals("User not found", result);
        verify(userRepository, never()).save(any(user.class));
    }

    @Test
    void resetPassword_whenUserFound_updatesPasswordAndReturnsSuccess() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        String result = authService.resetPassword("test@example.com", "newPass");

        assertEquals("Password reset successful", result);
        verify(userRepository).save(testUser);
    }

    // ---------- changePassword ----------
    @Test
    void changePassword_whenUserNotFound_returnsFalse() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        boolean result = authService.changePassword("test@example.com", "old", "new");

        assertFalse(result);
    }

    @Test
    void changePassword_whenCurrentPasswordWrong_returnsFalse() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("old", "encodedPass")).thenReturn(false);

        boolean result = authService.changePassword("test@example.com", "old", "new");

        assertFalse(result);
    }

    @Test
    void changePassword_whenValid_updatesPasswordAndReturnsTrue() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("old", "encodedPass")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("encodedNew");

        boolean result = authService.changePassword("test@example.com", "old", "new");

        assertTrue(result);
        verify(userRepository).save(testUser);
    }

    // ---------- getUserIfValid ----------
    @Test
    void getUserIfValid_whenCredentialsCorrect_returnsUser() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);

        user result = authService.getUserIfValid("test@example.com", "plainPass");

        assertNotNull(result);
    }

    @Test
    void getUserIfValid_whenPasswordIncorrect_returnsNull() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        user result = authService.getUserIfValid("test@example.com", "wrongPass");

        assertNull(result);
    }

    @Test
    void getUserIfValid_whenUserNotFound_returnsNull() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        user result = authService.getUserIfValid("test@example.com", "plainPass");

        assertNull(result);
    }
}