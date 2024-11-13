package com.project.G1_T3.email.service;

import com.project.G1_T3.common.exception.EmailServiceException;
import com.project.G1_T3.user.model.UserDTO;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendTempPasswordEmail_Success() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setEmail("test@example.com");
        String tempPassword = "tempPass123";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("AdminTempPasswordTemplate"), any())).thenReturn("HTML Content");

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendTempPasswordEmail(userDTO, tempPassword));

        // Verify
        verify(mailSender).send(any(MimeMessage.class));
        verify(templateEngine).process(eq("AdminTempPasswordTemplate"), any());
    }

    @Test
    void sendTempPasswordEmail_NullUserDTO() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendTempPasswordEmail(null, "tempPass"));
    }

    @Test
    void sendTempPasswordEmail_NullTempPassword() {
        UserDTO userDTO = new UserDTO();
        assertThrows(IllegalArgumentException.class, () -> emailService.sendTempPasswordEmail(userDTO, null));
    }

    @Test
    void sendVerificationEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String username = "testUser";
        String verificationLink = "http://localhost:3000/verify?token=12345";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("EmailVerificationTemplate"), any())).thenReturn("HTML Content");

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendVerificationEmail(to, username, verificationLink));

        // Verify
        verify(mailSender).send(any(MimeMessage.class));
        verify(templateEngine).process(eq("EmailVerificationTemplate"), any());
    }

    @Test
    void sendVerificationEmail_NullUsername() {
        String to = "test@example.com";
        String verificationLink = "http://localhost:3000/verify?token=12345";

        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendVerificationEmail(to, null, verificationLink));
    }

    @Test
    void sendVerificationEmail_NullVerificationLink() {
        String to = "test@example.com";
        String username = "testUser";

        assertThrows(IllegalArgumentException.class, () -> emailService.sendVerificationEmail(to, username, null));
    }

    @Test
    void sendEmail_Success() {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        assertDoesNotThrow(() -> emailService.sendEmail("test@example.com", "Test Subject", "Test Body", null));

        // Verify
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_NullTo() {
        assertThrows(EmailServiceException.class,
                () -> emailService.sendEmail(null, "Test Subject", "Test Body", null));
    }

    @Test
    void sendEmail_NullSubject() {
        assertThrows(EmailServiceException.class,
                () -> emailService.sendEmail("test@example.com", null, "Test Body", null));
    }

    @Test
    void sendEmail_NullBody() {
        assertThrows(EmailServiceException.class,
                () -> emailService.sendEmail("test@example.com", "Test Subject", null, null));
    }

}