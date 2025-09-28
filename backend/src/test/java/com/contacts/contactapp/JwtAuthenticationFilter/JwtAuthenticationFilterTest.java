package com.contacts.contactapp.JwtAuthenticationFilter;

import com.contacts.contactapp.JwtUtil.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_skipsAuthForPublicEndpoints() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Should call filterChain.doFilter without authentication set
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        String token = "valid.jwt.token";
        String email = "user@example.com";
        String role = "ROLE_USER";

        when(request.getRequestURI()).thenReturn("/api/contacts/data");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtUtil.getEmailFromToken(token)).thenReturn(email);
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn(role);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(email, auth.getPrincipal());
        assertEquals(Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role)), auth.getAuthorities());
    }

    @Test
    void doFilterInternal_invalidToken_noAuthentication() throws Exception {
        String token = "invalid.jwt.token";

        when(request.getRequestURI()).thenReturn("/api/contacts/data");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtUtil.getEmailFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_missingAuthHeader_noAuthentication() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/contacts/data");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}