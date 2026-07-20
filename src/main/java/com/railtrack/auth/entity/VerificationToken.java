package com.railtrack.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Transient OTP tracking table for the registration and forget-password
 * flows. This is completely additive to the existing {@link User} table -
 * a row here does not represent an account; it represents a pending,
 * time-boxed verification attempt. The OTP itself is never stored in
 * plaintext (see {@code otpCodeHash}), mirroring how {@link User#password}
 * is already handled with BCrypt.
 */
@Entity
@Table(name = "verification_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    /** BCrypt hash of the 6-digit OTP - never the raw code. */
    @Column(nullable = false)
    private String otpCodeHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationTokenType tokenType;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** Set true the moment an OTP is successfully used, so it can never be replayed even if still unexpired. */
    @Column(nullable = false)
    private boolean consumed;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
