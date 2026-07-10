package com.railtrack.auth.service;
import com.railtrack.auth.dto.request.UpdateProfileRequest;
import com.railtrack.auth.dto.response.UserResponse;
import com.railtrack.auth.entity.User;
public interface UserService {

    User getAuthenticatedUser();
    UserResponse getCurrentUser();
    UserResponse updateProfile(UpdateProfileRequest request);
    void deleteCurrentUser();
}
