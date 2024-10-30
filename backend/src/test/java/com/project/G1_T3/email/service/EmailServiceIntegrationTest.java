package com.project.G1_T3.email.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import com.project.G1_T3.user.model.UserDTO;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Part;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceIntegrationTest {

    private static final int SMTP_PORT = 3025;
    private static final ServerSetup SMTP_SERVER_SETUP = new ServerSetup(SMTP_PORT, "localhost", ServerSetup.PROTOCOL_SMTP);

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_SERVER_SETUP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("test@localhost", "secret")
                    .withDisabledAuthentication());

    @DynamicPropertySource
    static void configureMailServer(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> SMTP_PORT);
        registry.add("spring.mail.username", () -> "test@localhost");
        registry.add("spring.mail.password", () -> "secret");
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");
        registry.add("spring.mail.properties.mail.smtp.starttls.required", () -> "false");
        registry.add("spring.mail.properties.mail.debug", () -> "true");
    }

    @Autowired
    private EmailService emailService;

    private String getEmailContent(Part part) throws MessagingException, IOException {
        if (part.getContent() instanceof String) {
            return (String) part.getContent();
        }
        if (part.getContent() instanceof MimeMultipart) {
            MimeMultipart mimeMultipart = (MimeMultipart) part.getContent();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.getContent() instanceof String) {
                    result.append(bodyPart.getContent());
                } else if (bodyPart.getContent() instanceof MimeMultipart) {
                    result.append(getEmailContent(bodyPart));
                }
            }
            return result.toString();
        }
        return "";
    }

    @Test
    void whenSendingEmail_thenEmailIsActuallyReceived() throws MessagingException, IOException {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        emailService.sendEmail(to, subject, body, null).join();

        // Assert
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length, "Should have received exactly one email");
        assertEquals(subject, receivedMessages[0].getSubject());
        
        String emailContent = getEmailContent(receivedMessages[0]);
        assertTrue(emailContent.contains(body), 
            String.format("Email content should contain the body text '%s'. Actual content: '%s'", 
                body, emailContent));
    }

    @Test
    void whenSendingTempPassword_thenEmailIsActuallyReceived() throws MessagingException, IOException {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setEmail("recipient@example.com");
        String tempPassword = "TempPass123";

        // Act
        emailService.sendTempPasswordEmail(userDTO, tempPassword).join();

        // Assert
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length, "Should have received exactly one email");
        assertTrue(receivedMessages[0].getSubject().contains("Admin Account Created"));
        
        String emailContent = getEmailContent(receivedMessages[0]);
        assertTrue(emailContent.contains(userDTO.getUsername()), 
            String.format("Email should contain username '%s'. Actual content: '%s'", 
                userDTO.getUsername(), emailContent));
        assertTrue(emailContent.contains(tempPassword), 
            String.format("Email should contain temporary password '%s'. Actual content: '%s'", 
                tempPassword, emailContent));
    }

    @Test
    void whenSendingVerificationEmail_thenEmailIsActuallyReceived() throws MessagingException, IOException {
        // Arrange
        String to = "recipient@example.com";
        String username = "testUser";
        String verificationLink = "http://localhost:3000/verify?token=12345";

        // Act
        emailService.sendVerificationEmail(to, username, verificationLink).join();

        // Assert
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length, "Should have received exactly one email");
        assertTrue(receivedMessages[0].getSubject().contains("Verify Your Email"));
        
        String emailContent = getEmailContent(receivedMessages[0]);
        assertTrue(emailContent.contains(username), 
            String.format("Email should contain username '%s'. Actual content: '%s'", 
                username, emailContent));
        assertTrue(emailContent.contains(verificationLink), 
            String.format("Email should contain verification link '%s'. Actual content: '%s'", 
                verificationLink, emailContent));
    }
}