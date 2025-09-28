package com.contacts.contactapp.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    private final long EXPIRATION_TIME = 1000L * 60 * 60 * 10; // 10 hours


    @PostConstruct

    public void init() {
        try {
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);  // Automatically generates secure 512-bit key
            logger.info("JWT Secret key initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize JWT secret key: {}", e.getMessage());
        }
    }
    public String generateToken(String email, String role) {
        logger.info("Generating token for email: {} with role: {}", email, role);
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token, String email) {
        try {
            boolean valid = getEmailFromToken(token).equals(email) && !isTokenExpired(token);
            logger.debug("Validating token for email {}: {}", email, valid);
            return valid;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        String email = getClaims(token).getSubject();
        logger.debug("Extracted email from token: {}", email);
        return email;
    }

    public String extractRole(String token) {
        String role = getClaims(token).get("role", String.class);
        logger.debug("Extracted role from token: {}", role);
        return role;
    }

    private boolean isTokenExpired(String token) {
        boolean expired = getClaims(token).getExpiration().before(new Date());
        logger.debug("Token expired: {}", expired);
        return expired;
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Failed to parse claims from token: {}", e.getMessage());
            throw e;
        }
    }
}