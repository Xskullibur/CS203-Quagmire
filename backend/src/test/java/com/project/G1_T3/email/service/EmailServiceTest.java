package com.project.G1_T3.email.service;

import com.project.G1_T3.user.model.UserDTO;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    void sendEmail_Success() {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        assertDoesNotThrow(() -> emailService.sendEmail("test@example.com", "Test Subject", "Test Body"));

        // Verify
        verify(mailSender).send(any(MimeMessage.class));
    }
}