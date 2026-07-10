package com.railtrack.auth.service;
import com.railtrack.auth.dto.request.UpdateProfileRequest;
import com.railtrack.auth.dto.response.UserResponse;
public interface UserService {

    UserResponse getCurrentUser();
    UserResponse updateProfile(UpdateProfileRequest request);
}