package com.example.ratelimiter.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements the token bucket rate limiting algorithm.
 * Replenishes tokens at fixed intervals and permits requests if tokens are available.
 */
public class TokenBucketRateLimiter implements RateLimiterService {

    private final int capacity;
    private final int refillTokens;
    private final int refillPeriodInSeconds;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int capacity, int refillTokens, int refillPeriodInSeconds) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodInSeconds = refillPeriodInSeconds;
    }

    @Override
    public boolean isAllowed(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, Instant.now().getEpochSecond()));
        synchronized (bucket) {
            long now = Instant.now().getEpochSecond();
            long elapsed = now - bucket.lastRefillTime;
            if (elapsed >= refillPeriodInSeconds) {
                int refill = (int) (elapsed / refillPeriodInSeconds) * refillTokens;
                int newTokens = Math.min(bucket.tokens.get() + refill, capacity);
                bucket.tokens.set(newTokens);
                bucket.lastRefillTime = now;
            }
            if (bucket.tokens.get() > 0) {
                bucket.tokens.decrementAndGet();
                return true;
            }
            return false;
        }
    }

    private static class Bucket {
        AtomicInteger tokens;
        long lastRefillTime;
        Bucket(int tokens, long lastRefillTime) {
            this.tokens = new AtomicInteger(tokens);
            this.lastRefillTime = lastRefillTime;
        }
    }
}