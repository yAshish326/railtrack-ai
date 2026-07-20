package com.railtrack.auth.repository;

import com.railtrack.auth.entity.VerificationToken;
import com.railtrack.auth.entity.VerificationTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /** Most recent attempt for this email + flow, so verify/reset always checks against the latest OTP requested. */
    Optional<VerificationToken> findFirstByEmailAndTokenTypeOrderByCreatedAtDesc(
            String email, VerificationTokenType tokenType);

    /** Invalidate any earlier pending attempts before issuing a fresh OTP, so only the newest one is ever valid. */
    @Modifying
    @Query("delete from VerificationToken v where v.email = :email and v.tokenType = :tokenType")
    void deleteByEmailAndTokenType(
            @Param("email") String email, @Param("tokenType") VerificationTokenType tokenType);

    /** Housekeeping - safe to call periodically to purge stale, long-expired rows. */
    @Modifying
    @Query("delete from VerificationToken v where v.expiryTime < :cutoff")
    void deleteAllExpiredBefore(@Param("cutoff") LocalDateTime cutoff);
}
