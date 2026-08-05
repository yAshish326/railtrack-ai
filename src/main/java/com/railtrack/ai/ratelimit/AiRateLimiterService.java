package com.railtrack.ai.ratelimit;

/**
 * Per-user rate limiter for the AI Assistant chat endpoint.
 */
public interface AiRateLimiterService {

    /**
     * Checks whether the given user still has quota left, and if so,
     * consumes one unit of it. Throws {@link AiRateLimitExceededException}
     * if the user has hit their limit for the current window.
     */
    void checkAndConsume(Long userId);

    /**
     * Current usage snapshot for the given user, without consuming quota.
     */
    AiRateLimitStatus getStatus(Long userId);
}
