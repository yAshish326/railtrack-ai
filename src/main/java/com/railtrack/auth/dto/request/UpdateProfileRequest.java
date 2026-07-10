package com.railtrack.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * ============================================================================
 * DTO used for updating the logged-in user's profile.
 * ============================================================================
 */
public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required.")
    @Size(min = 3, max = 100,
            message = "Full name must be between 3 and 100 characters.")
    private String fullName;

    public UpdateProfileRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}