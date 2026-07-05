package com.railtrack.auth.service.impl;

import com.railtrack.auth.dto.request.UpdateProfileRequest;
import com.railtrack.auth.dto.response.UserResponse;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.mapper.UserMapper;
import com.railtrack.auth.repository.UserRepository;
import com.railtrack.auth.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
 * ============================================================================
 * UserServiceImpl
 * ----------------------------------------------------------------------------
 * Handles all business logic related to the logged-in user.
 *
 * Current Features:
 * 1. Get Logged-in User Profile
 * 2. Update Logged-in User Profile
 * ============================================================================
 */

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /*
     * =========================================================================
     * Returns the profile of the currently authenticated user.
     * =========================================================================
     */
    @Override
    public UserResponse getCurrentUser() {

        User user = getAuthenticatedUser();

        return userMapper.toResponse(user);
    }

    /*
     * =========================================================================
     * Updates the logged-in user's profile.
     * =========================================================================
     */
    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {

        // Get currently logged-in user
        User user = getAuthenticatedUser();

        // Update allowed fields
        user.setFullName(request.getFullName());

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Convert Entity to Response DTO
        return userMapper.toResponse(updatedUser);
    }

    /*
     * =========================================================================
     * Helper Method
     *
     * Returns the currently authenticated user from the database.
     *
     * Why?
     * Avoids duplicate code in multiple methods (DRY Principle).
     * =========================================================================
     */
    private User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found."));
    }
}