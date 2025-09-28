package com.contacts.contactapp.service;

import com.contacts.contactapp.emailservice.EmailService;
import com.contacts.contactapp.model.user;
import com.contacts.contactapp.JwtUtil.JwtUtil;
import com.contacts.contactapp.Repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private userRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------- Register User ----------
    public String registerUser(user user) {
        logger.info("Attempting to register user with email: {}", user.getEmail());

        Optional<user> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("Registration failed: Email already exists - {}", user.getEmail());
            return "Email already exists.";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getIsAdmin()) {
            user.setRole("ROLE_ADMIN");
            user.setAdmin(true);
        } else {
            user.setRole("ROLE_USER");
            user.setAdmin(false);
        }

        userRepository.save(user);
        logger.info("User registered successfully with email: {}", user.getEmail());
        return "User registered successfully.";
    }

    // ---------- Forgot Password ----------
    public String handleForgotPassword(String email) {
        logger.info("Handling forgot password for email: {}", email);

        Optional<user> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("Forgot password failed: No user found with email: {}", email);
            return "User not found with this email.";
        }

        String resetLink = "http://localhost:3000/reset-password?email=" + email;
        emailService.sendResetEmail(email, resetLink);
        logger.info("Password reset email sent to: {}", email);

        return "Password reset link has been sent to your email.";
    }

    // ---------- Reset Password ----------
    public String resetPassword(String email, String newPassword) {
        logger.info("Resetting password for email: {}", email);

        Optional<user> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            logger.warn("Reset password failed: User not found - {}", email);
            return "User not found";
        }

        user existingUser = optionalUser.get();
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
        logger.info("Password reset successful for user: {}", email);

        return "Password reset successful";
    }

    // ---------- Change Password ----------
    public boolean changePassword(String email, String currentPassword, String newPassword) {
        logger.info("Changing password for email: {}", email);

        Optional<user> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            logger.warn("Change password failed: User not found - {}", email);
            return false;
        }

        user user = optionalUser.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Change password failed: Current password incorrect for {}", email);
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password changed successfully for {}", email);
        return true;
    }

    // ---------- Validate Credentials & Get User (Used internally) ----------
    public user getUserIfValid(String email, String password) {
        logger.debug("Validating user credentials for {}", email);

        Optional<user> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            user user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                logger.info("Credentials valid for user: {}", email);
                return user;
            } else {
                logger.warn("Invalid password attempt for user: {}", email);
            }
        } else {
            logger.warn("User not found during login attempt: {}", email);
        }
        return null;
    }
}
