package com.example.ratelimiter.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
            long currentTime = Instant.now().getEpochSecond();
            long elapsedSeconds = currentTime - bucket.lastRefillTime;
            if (elapsedSeconds >= refillPeriodInSeconds) {
                int refillCount = (int)(elapsedSeconds / refillPeriodInSeconds) * refillTokens;
                int newTokens = Math.min(bucket.tokens.get() + refillCount, capacity);
                bucket.tokens.set(newTokens);
                bucket.lastRefillTime = currentTime;
            }
            if (bucket.tokens.get() > 0) {
                bucket.tokens.decrementAndGet();
                return true;
            } else {
                return false;
            }
        }
    }

    private static class Bucket {
        private AtomicInteger tokens;
        private long lastRefillTime;

        public Bucket(int tokens, long lastRefillTime) {
            this.tokens = new AtomicInteger(tokens);
            this.lastRefillTime = lastRefillTime;
        }
    }
}
