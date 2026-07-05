package com.railtrack.auth.service;

import com.railtrack.auth.dto.request.LoginRequest;
import com.railtrack.auth.dto.request.RegisterRequest;
import com.railtrack.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}