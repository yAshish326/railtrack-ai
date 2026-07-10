package com.railtrack.auth.service.impl;

import com.railtrack.ai.repository.AiHistoryRepository;
import com.railtrack.auth.dto.request.UpdateProfileRequest;
import com.railtrack.auth.dto.response.UserResponse;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.exception.UserNotFoundException;
import com.railtrack.auth.mapper.UserMapper;
import com.railtrack.auth.repository.UserRepository;
import com.railtrack.auth.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AiHistoryRepository aiHistoryRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           AiHistoryRepository aiHistoryRepository) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.aiHistoryRepository = aiHistoryRepository;
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

    /**
     * Deletes the currently authenticated user's account.
     */
    @Override
    @Transactional
    public void deleteCurrentUser() {
        User user = getAuthenticatedUser();

        aiHistoryRepository.deleteByUser(user);
        userRepository.delete(user);

        log.info("User account deleted successfully for email: {}",
                user.getEmail());
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
    @Override
    public User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found."));
    }
}
