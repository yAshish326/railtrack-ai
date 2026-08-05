package com.railtrack.ai.ratelimit;

import java.time.LocalDateTime;

/**
 * Snapshot of a user's AI Assistant quota at a point in time.
 */
public class AiRateLimitStatus {

    private final int limit;
    private final int used;
    private final int remaining;
    private final LocalDateTime resetAt;

    public AiRateLimitStatus(int limit, int used, LocalDateTime resetAt) {
        this.limit = limit;
        this.used = used;
        this.remaining = Math.max(0, limit - used);
        this.resetAt = resetAt;
    }

    public int getLimit() {
        return limit;
    }

    public int getUsed() {
        return used;
    }

    public int getRemaining() {
        return remaining;
    }

    public LocalDateTime getResetAt() {
        return resetAt;
    }
}
