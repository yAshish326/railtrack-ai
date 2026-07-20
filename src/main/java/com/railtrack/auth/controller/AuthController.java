package com.railtrack.auth.controller;

import com.railtrack.auth.dto.request.LoginRequest;
import com.railtrack.auth.dto.request.OtpRequest;
import com.railtrack.auth.dto.request.RegisterRequest;
import com.railtrack.auth.dto.request.RegisterVerifyRequest;
import com.railtrack.auth.dto.request.ResetPasswordRequest;
import com.railtrack.auth.dto.response.AuthMessageResponse;
import com.railtrack.auth.dto.response.AuthResponse;
import com.railtrack.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    // =====================================================================
    // Additive: OTP email verification for registration
    // =====================================================================

    @PostMapping("/register/send-otp")
    public ResponseEntity<AuthMessageResponse> sendRegistrationOtp(
            @RequestBody OtpRequest request) {

        AuthMessageResponse response = authService.sendRegistrationOtp(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/verify")
    public ResponseEntity<AuthResponse> verifyRegistration(
            @RequestBody RegisterVerifyRequest request) {

        AuthResponse response = authService.verifyRegistrationOtp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================================
    // Additive: forget-password flow
    // =====================================================================

    @PostMapping("/password/forgot")
    public ResponseEntity<AuthMessageResponse> forgotPassword(
            @RequestBody OtpRequest request) {

        AuthMessageResponse response = authService.sendPasswordResetOtp(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<AuthMessageResponse> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        AuthMessageResponse response = authService.resetPassword(request);

        return ResponseEntity.ok(response);
    }
}
