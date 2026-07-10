package com.railtrack.ai.dto;

import jakarta.validation.constraints.NotBlank;

/*
 * ============================================================================
 * Request DTO for AI Chat.
 * ============================================================================
 */
public class AiChatRequest {

    @NotBlank(message = "Message cannot be empty.")
    private String message;

    public AiChatRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}