package com.railtrack.auth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class OtpMailService {

    private static final Logger log = LoggerFactory.getLogger(OtpMailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public OtpMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Fire-and-forget: the OTP is already generated and saved to the DB by
     * the caller *before* this runs, so a slow/failed email send no longer
     * blocks or fails the /send-otp HTTP response. Failures are logged here
     * (and nowhere else) - check logs if users report "OTP never arrived".
     */
    @Async("otpMailExecutor")
    public void sendRegistrationOtp(String toEmail, String otpCode) {
        send(toEmail, "RailTrack AI - Verify your email",
                "Your RailTrack AI registration OTP is: " + otpCode
                        + "\n\nThis code expires in 10 minutes. "
                        + "If you did not request this, you can safely ignore this email.");
    }

    @Async("otpMailExecutor")
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
            // Async context: nothing is listening for a thrown exception here,
            // so log it clearly instead of throwing into the void.
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage(), ex);
        }
    }
}