package com.railtrack.auth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Sends OTP emails for the registration and forget-password flows. Kept as
 * its own small component (rather than inline in AuthServiceImpl) so the
 * mail concern can be swapped out (e.g. for a templated HTML mail, or a
 * transactional email provider) without touching auth logic.
 */
@Component
public class OtpMailService {

    private static final Logger log = LoggerFactory.getLogger(OtpMailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public OtpMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationOtp(String toEmail, String otpCode) {
        send(toEmail, "RailTrack AI - Verify your email",
                "Your RailTrack AI registration OTP is: " + otpCode
                        + "\n\nThis code expires in 10 minutes. "
                        + "If you did not request this, you can safely ignore this email.");
    }

    public void sendPasswordResetOtp(String toEmail, String otpCode) {
        send(toEmail, "RailTrack AI - Password reset code",
                "Your RailTrack AI password reset OTP is: " + otpCode
                        + "\n\nThis code expires in 10 minutes. "
                        + "If you did not request a password reset, you can safely ignore this email.");
    }

    private void send(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); // Fix: Explicitly set the sender address
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("OTP email dispatched to {}", toEmail);
        } catch (MailException ex) {
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
            throw new IllegalStateException("Could not send verification email. Please try again shortly.");
        }
    }
}