package com.railtrack.auth.util;

import java.security.SecureRandom;

public final class OtpGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private OtpGenerator() {
    }

    /** Generates a zero-padded 6-digit OTP (000000-999999) using a cryptographically secure RNG. */
    public static String generate() {
        int code = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
