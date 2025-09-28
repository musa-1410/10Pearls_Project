package com.contacts.contactapp.emailservice;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void testSendResetEmail() {
        String toEmail = "test@example.com";
        String resetLink = "http://localhost:3000/reset-password/token123";

        emailService.sendResetEmail(toEmail, resetLink);

        // Capture the argument sent to mailSender.send()
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Reset Your Password", sentMessage.getSubject());
        assertEquals("Click this link to reset your password: " + resetLink, sentMessage.getText());
    }
}