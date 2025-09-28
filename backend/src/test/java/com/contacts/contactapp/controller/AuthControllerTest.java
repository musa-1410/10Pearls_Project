package com.contacts.contactapp.controller;

import com.contacts.contactapp.dto.PasswordChangeRequest;
import com.contacts.contactapp.model.user;
import com.contacts.contactapp.Repository.userRepository;
import com.contacts.contactapp.service.AuthService;
import com.contacts.contactapp.JwtUtil.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Load only AuthController and configure MockMvc (disable filters if Security blocks tests)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private userRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @Test
    void testRegister() throws Exception {
        when(authService.registerUser(any(user.class))).thenReturn("User registered successfully");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void testLogin_Success() throws Exception {
        user u = new user();
        u.setEmail("test@example.com");
        u.setPassword("encodedPass");
        u.setRole("ROLE_USER");
        u.setName("Maryam");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("1234", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "ROLE_USER")).thenReturn("fake-jwt");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testLogin_Failure() throws Exception {
        when(userRepository.findByEmail("fail@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"fail@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testForgotPassword() throws Exception {
        when(authService.handleForgotPassword("test@example.com")).thenReturn("Email sent");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent"));
    }

    @Test
    void testResetPassword() throws Exception {
        when(authService.resetPassword("test@example.com", "newPass")).thenReturn("Password reset successful");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"newPass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successful"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        when(authService.changePassword("test@example.com", "oldPass", "newPass")).thenReturn(true);

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"currentPassword\":\"oldPass\",\"newPassword\":\"newPass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    void testChangePassword_Failure() throws Exception {
        when(authService.changePassword("test@example.com", "oldPass", "newPass")).thenReturn(false);

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"currentPassword\":\"oldPass\",\"newPassword\":\"newPass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Current password is incorrect"));
    }
}}