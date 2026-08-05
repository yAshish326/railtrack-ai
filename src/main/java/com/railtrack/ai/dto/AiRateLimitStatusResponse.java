package com.railtrack.ai.dto;

import java.time.LocalDateTime;

/**
 * Response DTO exposing the current user's AI Assistant quota, so the
 * frontend can show something like "12 / 20 messages used today".
 */
public class AiRateLimitStatusResponse {

    private int limit;
    private int used;
    private int remaining;
    private LocalDateTime resetAt;

    public AiRateLimitStatusResponse() {
    }

    public AiRateLimitStatusResponse(int limit, int used, int remaining, LocalDateTime resetAt) {
        this.limit = limit;
        this.used = used;
        this.remaining = remaining;
        this.resetAt = resetAt;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public LocalDateTime getResetAt() {
        return resetAt;
    }

    public void setResetAt(LocalDateTime resetAt) {
        this.resetAt = resetAt;
    }
}
