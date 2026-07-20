package com.railtrack.auth.service;

import com.railtrack.auth.dto.request.LoginRequest;
import com.railtrack.auth.dto.request.OtpRequest;
import com.railtrack.auth.dto.request.RegisterRequest;
import com.railtrack.auth.dto.request.RegisterVerifyRequest;
import com.railtrack.auth.dto.request.ResetPasswordRequest;
import com.railtrack.auth.dto.response.AuthMessageResponse;
import com.railtrack.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    // ---------------------------------------------------------------
    // Additive: OTP email verification for registration
    // ---------------------------------------------------------------
    AuthMessageResponse sendRegistrationOtp(OtpRequest request);

    AuthResponse verifyRegistrationOtp(RegisterVerifyRequest request);

    // ---------------------------------------------------------------
    // Additive: forget-password flow
    // ---------------------------------------------------------------
    AuthMessageResponse sendPasswordResetOtp(OtpRequest request);

    AuthMessageResponse resetPassword(ResetPasswordRequest request);
}