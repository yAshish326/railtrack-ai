package com.railtrack.ai.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory, per-user, fixed daily window rate limiter for the AI Assistant.
 *
 * How it works:
 * - One {@link Bucket} is kept per user id, holding a request count and the
 *   calendar day that count applies to.
 * - On every check, if the stored day no longer matches "today", the bucket
 * is silently reset to zero before being evaluated - this gives us a
 * self-resetting daily quota without a scheduled job.
 * - Access to a single user's bucket is synchronized on that bucket object,
 * so concurrent requests from the same user can't both slip through at the
 * boundary of the limit.
 *
 * Trade-off: this resets on app restart and does not share state across
 * multiple backend instances. That's fine for a single-instance deployment
 * (e.g. one Render/Railway dyno). If this ever runs on more than one
 * instance, swap this out for a DB-backed or Redis-backed counter behind
 * the same {@link AiRateLimiterService} interface - nothing else in the
 * app needs to change.
 */
@Service
public class InMemoryAiRateLimiterService implements AiRateLimiterService {

    private final ConcurrentHashMap<Long, Bucket> buckets = new ConcurrentHashMap<>();

    private final int maxRequestsPerDay;

    public InMemoryAiRateLimiterService(
            @Value("${ai.assistant.rate-limit.max-requests-per-day:20}") int maxRequestsPerDay) {
        this.maxRequestsPerDay = maxRequestsPerDay;
    }

    @Override
    public void checkAndConsume(Long userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, id -> new Bucket());

        synchronized (bucket) {
            resetIfNewDay(bucket);

            if (bucket.count >= maxRequestsPerDay) {
                throw new AiRateLimitExceededException(maxRequestsPerDay, startOfNextDay());
            }

            bucket.count++;
        }
    }

    @Override
    public AiRateLimitStatus getStatus(Long userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, id -> new Bucket());

        synchronized (bucket) {
            resetIfNewDay(bucket);
            return new AiRateLimitStatus(maxRequestsPerDay, bucket.count, startOfNextDay());
        }
    }

    private void resetIfNewDay(Bucket bucket) {
        LocalDate today = LocalDate.now();
        if (!today.equals(bucket.day)) {
            bucket.day = today;
            bucket.count = 0;
        }
    }

    private LocalDateTime startOfNextDay() {
        return LocalDate.now().plusDays(1).atTime(LocalTime.MIDNIGHT);
    }

    /** Mutable per-user counter. Never exposed outside this class. */
    private static final class Bucket {
        private LocalDate day = LocalDate.now();
        private int count = 0;
    }
}
