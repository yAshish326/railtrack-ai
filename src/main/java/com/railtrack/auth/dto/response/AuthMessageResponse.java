package com.railtrack.auth.dto.response;

/** Response DTO for the OTP send/verify and password reset flows. */
public record AuthMessageResponse(String message) {
}
