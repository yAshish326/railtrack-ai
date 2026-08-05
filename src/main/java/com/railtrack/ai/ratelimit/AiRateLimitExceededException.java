package com.railtrack.ai.ratelimit;

import java.time.LocalDateTime;

/**
 * Thrown when a user has exhausted their AI Assistant chat quota for the
 * current window. Carries enough context (limit, reset time) for the
 * GlobalExceptionHandler to build a useful 429 response.
 */
public class AiRateLimitExceededException extends RuntimeException {

    private final int limit;
    private final LocalDateTime resetAt;

    public AiRateLimitExceededException(int limit, LocalDateTime resetAt) {
        super("You've reached your limit of " + limit +
                " AI Assistant messages for today. Please try again after " +
                resetAt + ".");
        this.limit = limit;
        this.resetAt = resetAt;
    }

    public int getLimit() {
        return limit;
    }

    public LocalDateTime getResetAt() {
        return resetAt;
    }
}
