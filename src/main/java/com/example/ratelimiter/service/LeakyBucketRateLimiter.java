package com.example.ratelimiter.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements the leaky bucket rate limiting algorithm.
 * Ensures a constant outflow rate by draining tokens over time.
 */
public class LeakyBucketRateLimiter implements RateLimiterService {

    private final int capacity;
    private final int leakRatePerSecond;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public LeakyBucketRateLimiter(int capacity, int leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
    }

    @Override
    public boolean isAllowed(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, Instant.now().getEpochSecond()));
        synchronized (bucket) {
            long now = Instant.now().getEpochSecond();
            long elapsed = now - bucket.lastLeakTime;
            if (elapsed > 0) {
                int leaked = (int) (elapsed * leakRatePerSecond);
                int remaining = Math.max(bucket.tokens.get() - leaked, 0);
                bucket.tokens.set(remaining);
                bucket.lastLeakTime = now;
            }
            if (bucket.tokens.get() < capacity) {
                bucket.tokens.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    private static class Bucket {
        AtomicInteger tokens;
        long lastLeakTime;
        Bucket(int tokens, long lastLeakTime) {
            this.tokens = new AtomicInteger(tokens);
            this.lastLeakTime = lastLeakTime;
        }
    }
}