package com.railtrack.auth.dto.request;

/** Request used to check a password-reset OTP before collecting a new password. */
public class VerifyOtpRequest {

    private String email;
    private String otpCode;

    public VerifyOtpRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
