package com.contacts.contactapp.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private byte[] secretKeyBytes;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Generate secret key for HS512 algorithm
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        secretKeyBytes = key.getEncoded();

        // Encode key as base64 string (required by JwtUtil)
        String base64Secret = Base64.getEncoder().encodeToString(secretKeyBytes);

        // Inject the secret key string into jwtUtil instance
        ReflectionTestUtils.setField(jwtUtil, "secret", base64Secret);

        // Initialize key inside JwtUtil (calls init())
        jwtUtil.init();
    }

    // Helper to generate token with custom expiry time (for testing expiration)
    private String generateTokenWithCustomExpiry(String email, String role, long expiryInMillis) {
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + expiryInMillis))
                .signWith(Keys.hmacShaKeyFor(secretKeyBytes), SignatureAlgorithm.HS512)
                .compact();
    }

    @Test
    void generateToken_and_getEmailAndRole() {
        String email = "user@example.com";
        String role = "ROLE_USER";

        String token = jwtUtil.generateToken(email, role);
        assertNotNull(token);

        String extractedEmail = jwtUtil.getEmailFromToken(token);
        String extractedRole = jwtUtil.extractRole(token);

        assertEquals(email, extractedEmail);
        assertEquals(role, extractedRole);
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String email = "validuser@example.com";
        String role = "ROLE_ADMIN";

        String token = jwtUtil.generateToken(email, role);
        assertTrue(jwtUtil.validateToken(token, email));
    }

    @Test
    void validateToken_invalidEmail_returnsFalse() {
        String email = "user@example.com";
        String otherEmail = "other@example.com";
        String role = "ROLE_USER";

        String token = jwtUtil.generateToken(email, role);
        assertFalse(jwtUtil.validateToken(token, otherEmail));
    }

    @Test
    void getEmailFromToken_expiredToken_throwsExpiredJwtException() throws InterruptedException {
        // Generate token that expires after 1 second
        String token = generateTokenWithCustomExpiry("test@example.com", "ROLE_USER", 1000);

        // Wait so that token expires
        Thread.sleep(1500);

        // Now calling getEmailFromToken should throw ExpiredJwtException
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.getEmailFromToken(token));
    }

    @Test
    void getEmailFromToken_invalidToken_throwsException() {
        String invalidToken = "invalid.token.string";

        assertThrows(Exception.class, () -> jwtUtil.getEmailFromToken(invalidToken));
    }

    @Test
    void extractRole_invalidToken_throwsException() {
        String invalidToken = "invalid.token.string";

        assertThrows(Exception.class, () -> jwtUtil.extractRole(invalidToken));
    }
}