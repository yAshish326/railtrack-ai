package com.railtrack.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * ============================================================================
 * DTO used for changing the logged-in user's password.
 * ============================================================================
 */
public class ChangePasswordRequest {

    /*
     * User's current password.
     * Used to verify the user's identity.
     */
    @NotBlank(message = "Current password is required.")
    private String currentPassword;

    /*
     * User's new password.
     */
    @NotBlank(message = "New password is required.")
    @Size(min = 8, max = 50,
            message = "Password must be between 8 and 50 characters.")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}