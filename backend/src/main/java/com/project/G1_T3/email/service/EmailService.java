package com.project.G1_T3.email.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.project.G1_T3.common.exception.EmailServiceException;
import com.project.G1_T3.user.model.UserDTO;

import org.springframework.core.io.ClassPathResource;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public CompletableFuture<Void> sendTempPasswordEmail(UserDTO userDTO, String tempPassword) {

        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO cannot be null.");
        }

        if (tempPassword == null) {
            throw new IllegalArgumentException("tempPassword cannot be null.");
        }

        Context context = new Context();
        context.setVariable("name", userDTO.getUsername());
        context.setVariable("loginUrl", frontendUrl + "/auth/login");
        context.setVariable("tempPassword", tempPassword);

        String htmlContent = templateEngine.process("AdminTempPasswordTemplate", context);

        Map<String, ClassPathResource> inlineResources = new HashMap<>();
        inlineResources.put("email_icon", new ClassPathResource("templates/static/heroGIF.png"));
        inlineResources.put("welcome_image", new ClassPathResource("templates/static/Hello-rafiki.png"));

        sendEmail(userDTO.getEmail(), "Admin Account Created", htmlContent, inlineResources);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendVerificationEmail(String to, String username, String verificationLink) {

        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }

        if (verificationLink == null) {
            throw new IllegalArgumentException("VerificationLink cannot be null.");
        }

        Context context = new Context();
        context.setVariable("name", username);
        context.setVariable("verificationLink", verificationLink);

        String htmlContent = templateEngine.process("EmailVerificationTemplate", context);

        Map<String, ClassPathResource> inlineResources = new HashMap<>();
        inlineResources.put("email_icon", new ClassPathResource("templates/static/heroGIF.png"));
        inlineResources.put("welcome_image", new ClassPathResource("templates/static/Hello-rafiki.png"));

        sendEmail(to, "Verify Your Email", htmlContent, inlineResources);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject, String body,
            Map<String, ClassPathResource> inlineResources) {
        try {

            if (to == null) {
                throw new IllegalArgumentException("Recipient email address cannot be null.");
            }

            if (subject == null) {
                throw new IllegalArgumentException("Email subject cannot be null.");
            }

            if (body == null) {
                throw new IllegalArgumentException("Email body cannot be null.");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            if (inlineResources != null) {
                for (Map.Entry<String, ClassPathResource> entry : inlineResources.entrySet()) {
                    helper.addInline(entry.getKey(), entry.getValue());
                }
            }

            log.info("Attempting to send email to: {}", to);
            mailSender.send(message);
            log.info("Successfully sent email to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
            log.info("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
            throw new EmailServiceException(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}. Error: {}", to, e.getMessage(), e);
            log.info("Unexpected error while sending email to: {}. Error: {}", to, e.getMessage(), e);
            throw new EmailServiceException("Unexpected error while sending email: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }
}
