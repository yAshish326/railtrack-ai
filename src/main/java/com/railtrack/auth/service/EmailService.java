package com.railtrack.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.from.email:aashishyadav326@gmail.com}")
    private String fromEmail;

    private final RestClient restClient;

    public EmailService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    public void sendRegistrationOtp(String toEmail, String otpCode) {
        String subject = "Your RailTrack AI Registration Code";
        String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Welcome to RailTrack AI!</h2>
                    <p>Your OTP code for registration is:</p>
                    <h1 style="color: #2563eb; letter-spacing: 2px;">%s</h1>
                    <p>This code will expire in 10 minutes.</p>
                </div>
                """.formatted(otpCode);

        sendEmailViaBrevo(toEmail, subject, htmlContent);
    }

    public void sendPasswordResetOtp(String toEmail, String otpCode) {
        String subject = "Your RailTrack AI Password Reset Code";
        String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Password Reset Request</h2>
                    <p>Your OTP code to reset your password is:</p>
                    <h1 style="color: #dc2626; letter-spacing: 2px;">%s</h1>
                    <p>This code will expire in 10 minutes. If you did not request this, please ignore this email.</p>
                </div>
                """.formatted(otpCode);

        sendEmailViaBrevo(toEmail, subject, htmlContent);
    }

    private void sendEmailViaBrevo(String toEmail, String subject, String htmlContent) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "sender", Map.of("name", "RailTrack AI", "email", fromEmail),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", subject,
                    "htmlContent", htmlContent
            );

            restClient.post()
                    .uri("/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Email successfully dispatched via Brevo API to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email from Brevo API to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email. Please try again later.", e);
        }
    }
}