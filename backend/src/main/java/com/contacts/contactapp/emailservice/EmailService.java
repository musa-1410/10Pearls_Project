package com.contacts.contactapp.emailservice;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String toEmail, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Reset Your Password");
            message.setText("Click this link to reset your password: " + resetLink);
            mailSender.send(message);
            logger.info("Reset password email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send reset email to {}: {}", toEmail, e.getMessage());
        }
    }

