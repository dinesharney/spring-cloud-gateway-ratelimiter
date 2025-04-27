package com.example.ratelimiter.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
            long currentTime = Instant.now().getEpochSecond();
            long elapsedSeconds = currentTime - bucket.lastLeakTime;
            if (elapsedSeconds > 0) {
                int leaked = (int)(elapsedSeconds * leakRatePerSecond);
                int newTokens = Math.max(bucket.tokens.get() - leaked, 0);
                bucket.tokens.set(newTokens);
                bucket.lastLeakTime = currentTime;
            }
            if (bucket.tokens.get() < capacity) {
                bucket.tokens.incrementAndGet();
                return true;
            } else {
                return false;
            }
        }
    }

    private static class Bucket {
        private AtomicInteger tokens;
        private long lastLeakTime;

        public Bucket(int tokens, long lastLeakTime) {
            this.tokens = new AtomicInteger(tokens);
            this.lastLeakTime = lastLeakTime;
        }
    }
}
