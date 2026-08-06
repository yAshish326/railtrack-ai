package com.railtrack.auth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OtpMailService {

    private static final Logger log = LoggerFactory.getLogger(OtpMailService.class);

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    private final RestClient restClient;

    public OtpMailService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .build();
    }

    @Async("otpMailExecutor")
    public void sendRegistrationOtp(String toEmail, String otpCode) {
        String subject = "RailTrack AI - Verify your email";
        String htmlBody = """
                <div style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Welcome to RailTrack AI!</h2>
                    <p>Your registration verification code is:</p>
                    <h1 style="color: #2563eb; letter-spacing: 2px;">%s</h1>
                    <p>This code expires in 10 minutes. If you did not request this, you can safely ignore this email.</p>
                </div>
                """.formatted(otpCode);

        send(toEmail, subject, htmlBody);
    }

    @Async("otpMailExecutor")
    public void sendPasswordResetOtp(String toEmail, String otpCode) {
        String subject = "RailTrack AI - Password reset code";
        String htmlBody = """
                <div style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Password Reset Request</h2>
                    <p>Your password reset code is:</p>
                    <h1 style="color: #dc2626; letter-spacing: 2px;">%s</h1>
                    <p>This code expires in 10 minutes. If you did not request a password reset, you can safely ignore this email.</p>
                </div>
                """.formatted(otpCode);

        send(toEmail, subject, htmlBody);
    }

    private void send(String toEmail, String subject, String htmlBody) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "sender", Map.of("name", "RailTrack AI", "email", fromEmail),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", subject,
                    "htmlContent", htmlBody
            );

            restClient.post()
                    .uri("/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();

            log.info("OTP email successfully dispatched via Brevo API to {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send OTP email via Brevo API to {}: {}", toEmail, ex.getMessage(), ex);
        }
    }
}