package com.railtrack.auth.controller;

import com.railtrack.auth.dto.request.UpdateProfileRequest;
import com.railtrack.auth.dto.response.UserResponse;
import com.railtrack.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 * ============================================================================
 * UserController
 * ----------------------------------------------------------------------------
 * Handles all user-related APIs.
 * ============================================================================
 */

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*
     * Get Logged-in User Profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUser() {

        UserResponse response = userService.getCurrentUser();

        return ResponseEntity.ok(response);
    }

    /*
     * =========================================================================
     * Update Logged-in User Profile
     * =========================================================================
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        UserResponse response =
                userService.updateProfile(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete Logged-in User Account
     */
    @DeleteMapping("/profile")
    public ResponseEntity<Map<String, String>> deleteCurrentUser() {
        userService.deleteCurrentUser();

        return ResponseEntity.ok(
                Map.of("message", "User account deleted successfully.")
        );
    }
}
