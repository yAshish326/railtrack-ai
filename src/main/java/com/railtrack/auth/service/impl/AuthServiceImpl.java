package com.railtrack.auth.service.impl;

import com.railtrack.auth.dto.request.LoginRequest;
import com.railtrack.auth.dto.request.OtpRequest;
import com.railtrack.auth.dto.request.RegisterRequest;
import com.railtrack.auth.dto.request.RegisterVerifyRequest;
import com.railtrack.auth.dto.request.ResetPasswordRequest;
import com.railtrack.auth.dto.response.AuthMessageResponse;
import com.railtrack.auth.dto.response.AuthResponse;
import com.railtrack.auth.dto.response.UserResponse;
import com.railtrack.auth.entity.Role;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.entity.VerificationToken;
import com.railtrack.auth.entity.VerificationTokenType;
import com.railtrack.auth.exception.InvalidCredentialsException;
import com.railtrack.auth.exception.UserAlreadyExistsException;
import com.railtrack.auth.jwt.JwtService;
import com.railtrack.auth.mapper.UserMapper;
import com.railtrack.auth.repository.UserRepository;
import com.railtrack.auth.repository.VerificationTokenRepository;
import com.railtrack.auth.service.AuthService;
import com.railtrack.auth.util.OtpGenerator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final Pattern GMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@gmail\\.com$", Pattern.CASE_INSENSITIVE);
    private static final int OTP_EXPIRY_MINUTES = 10;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository verificationTokenRepository;
    private final OtpMailService otpMailService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            VerificationTokenRepository verificationTokenRepository,
            OtpMailService otpMailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenRepository = verificationTokenRepository;
        this.otpMailService = otpMailService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        log.info("Register request received for email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn("Registration failed. Email already exists: {}", request.getEmail());

            throw new UserAlreadyExistsException(
                    "Email already registered."
            );
        }

        // Convert DTO to Entity
        User user = userMapper.toEntity(request);

        // Encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Default role
        user.setRole(Role.USER);

        // Save user
        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getEmail());

        // Generate JWT
        String token = jwtService.generateToken(savedUser);

        // Convert Entity to DTO
        UserResponse userResponse = userMapper.toResponse(savedUser);

        // Return JWT + User
        return new AuthResponse(
                "User registered successfully.",
                token,
                userResponse
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Login request received for email: {}", request.getEmail());

        try {

            // Authenticate username & password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (BadCredentialsException ex) {

            log.warn("Invalid login attempt for email: {}", request.getEmail());

            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );
        }

        // Load authenticated user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password."
                        ));

        // Generate JWT
        String token = jwtService.generateToken(user);

        // Convert Entity to DTO
        UserResponse userResponse = userMapper.toResponse(user);

        log.info("User logged in successfully: {}", user.getEmail());

        // Return JWT + User
        return new AuthResponse(
                "Login successful.",
                token,
                userResponse
        );
    }

    // =====================================================================
    // Additive: OTP email verification for registration
    // =====================================================================

    @Override
    @Transactional
    public AuthMessageResponse sendRegistrationOtp(OtpRequest request) {

        String email = validateGmailAddress(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration OTP requested for an already-registered email: {}", email);
            throw new UserAlreadyExistsException("Email already registered.");
        }

        issueOtp(email, VerificationTokenType.REGISTRATION);

        log.info("Registration OTP issued for email: {}", email);

        return new AuthMessageResponse(
                "OTP sent to your email. It will expire in " + OTP_EXPIRY_MINUTES + " minutes.");
    }

    @Override
    @Transactional
    public AuthResponse verifyRegistrationOtp(RegisterVerifyRequest request) {

        String email = validateGmailAddress(request.getEmail());

        VerificationToken token = consumeValidOtp(email, VerificationTokenType.REGISTRATION, request.getOtpCode());

        // Defensive re-check: guards against a duplicate registration slipping
        // in between OTP request and verification.
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration verification blocked - email already registered: {}", email);
            throw new UserAlreadyExistsException("Email already registered.");
        }

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        log.info("User registered successfully via OTP verification: {}", savedUser.getEmail());

        String jwt = jwtService.generateToken(savedUser);
        UserResponse userResponse = userMapper.toResponse(savedUser);

        markConsumed(token);

        return new AuthResponse(
                "Email verified. Account created successfully.",
                jwt,
                userResponse
        );
    }

    // =====================================================================
    // Additive: forget-password flow
    // =====================================================================

    @Override
    @Transactional
    public AuthMessageResponse sendPasswordResetOtp(OtpRequest request) {

        String email = validateGmailAddress(request.getEmail());

        // Intentionally the same response whether or not the account exists,
        // so this endpoint can't be used to enumerate registered emails.
        AuthMessageResponse genericResponse = new AuthMessageResponse(
                "If an account exists with this email, a password reset code has been sent.");

        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    issueOtp(email, VerificationTokenType.FORGET_PASSWORD);
                    log.info("Password reset OTP issued for email: {}", email);
                },
                () -> log.info("Password reset requested for unregistered email: {}", email)
        );

        return genericResponse;
    }

    @Override
    @Transactional
    public AuthMessageResponse resetPassword(ResetPasswordRequest request) {

        String email = validateGmailAddress(request.getEmail());

        VerificationToken token = consumeValidOtp(email, VerificationTokenType.FORGET_PASSWORD, request.getOtpCode());

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Account no longer exists for this email."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        markConsumed(token);

        log.info("Password reset successfully for email: {}", email);

        return new AuthMessageResponse(
                "Password reset successful. You can now log in with your new password.");
    }

    // =====================================================================
    // Shared OTP helpers
    // =====================================================================

    /** Enforces @gmail.com-only addresses; returns the normalized (lowercase) email. */
    private String validateGmailAddress(String email) {
        if (email == null || !GMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Only valid @gmail.com addresses are accepted.");
        }
        return email.trim().toLowerCase();
    }

    /** Invalidates any earlier pending OTPs for this email+flow, then generates, stores, and emails a fresh one. */
    private void issueOtp(String email, VerificationTokenType tokenType) {

        verificationTokenRepository.deleteByEmailAndTokenType(email, tokenType);

        String rawOtp = OtpGenerator.generate();
        String otpHash = passwordEncoder.encode(rawOtp);

        VerificationToken token = VerificationToken.builder()
                .email(email)
                .otpCodeHash(otpHash)
                .tokenType(tokenType)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .consumed(false)
                .build();

        verificationTokenRepository.save(token);

        if (tokenType == VerificationTokenType.REGISTRATION) {
            otpMailService.sendRegistrationOtp(email, rawOtp);
        } else {
            otpMailService.sendPasswordResetOtp(email, rawOtp);
        }
    }

    /** Validates the OTP (exists, not consumed, not expired, matches) and returns the token - does not mark it consumed yet. */
    private VerificationToken consumeValidOtp(String email, VerificationTokenType tokenType, String suppliedOtp) {

        if (suppliedOtp == null || suppliedOtp.isBlank()) {
            throw new IllegalArgumentException("OTP code is required.");
        }

        VerificationToken token = verificationTokenRepository
                .findFirstByEmailAndTokenTypeOrderByCreatedAtDesc(email, tokenType)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No OTP request found for this email. Please request a new OTP."));

        if (token.isConsumed()) {
            throw new IllegalArgumentException("This OTP has already been used. Please request a new one.");
        }

        if (token.isExpired()) {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }

        if (!passwordEncoder.matches(suppliedOtp.trim(), token.getOtpCodeHash())) {
            throw new IllegalArgumentException("Invalid OTP.");
        }

        return token;
    }

    private void markConsumed(VerificationToken token) {
        token.setConsumed(true);
        verificationTokenRepository.save(token);
    }
}