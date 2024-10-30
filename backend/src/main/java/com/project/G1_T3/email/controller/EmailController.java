package com.project.G1_T3.email.controller;

import com.project.G1_T3.email.model.EmailRequest;
import com.project.G1_T3.email.model.EmailResponse;
import com.project.G1_T3.email.service.EmailService;
import com.project.G1_T3.common.exception.EmailServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("/basic")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            // Input validation
            if (emailRequest.getTo() == null || emailRequest.getTo().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new EmailResponse("Recipient email address is required", false));
            }
            if (emailRequest.getSubject() == null || emailRequest.getSubject().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new EmailResponse("Email subject is required", false));
            }
            if (emailRequest.getBody() == null || emailRequest.getBody().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new EmailResponse("Email body is required", false));
            }

            // Send email asynchronously
            CompletableFuture<Void> future = emailService.sendEmail(
                    emailRequest.getTo(),
                    emailRequest.getSubject(),
                    emailRequest.getBody(),
                    null // No inline resources for this simple endpoint
            );

            // Wait for the completion
            future.get(); // This will throw an exception if the email fails to send

            logger.info("Email sent successfully to: {}", emailRequest.getTo());
            return ResponseEntity.ok(new EmailResponse("Email sent successfully", true));

        } catch (EmailServiceException e) {
            logger.error("Failed to send email: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new EmailResponse("Failed to send email: " + e.getMessage(), false));
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error while sending email: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(new EmailResponse("Error while sending email: " + e.getMessage(), false));
        }
    }
}