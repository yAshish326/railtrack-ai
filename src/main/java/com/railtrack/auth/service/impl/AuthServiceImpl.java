package com.railtrack.auth.service.impl;

import com.railtrack.auth.dto.request.LoginRequest;
import com.railtrack.auth.dto.request.RegisterRequest;
import com.railtrack.auth.dto.response.AuthResponse;
import com.railtrack.auth.dto.response.UserResponse;
import com.railtrack.auth.entity.Role;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.exception.InvalidCredentialsException;
import com.railtrack.auth.exception.UserAlreadyExistsException;
import com.railtrack.auth.jwt.JwtService;
import com.railtrack.auth.mapper.UserMapper;
import com.railtrack.auth.repository.UserRepository;
import com.railtrack.auth.service.AuthService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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
}