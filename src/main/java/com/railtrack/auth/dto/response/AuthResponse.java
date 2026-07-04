package com.railtrack.auth.dto.response;

public class AuthResponse {

    // Success message
    private String message;

    // JWT Token
    private String token;

    // Logged-in user information
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String message, String token, UserResponse user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}