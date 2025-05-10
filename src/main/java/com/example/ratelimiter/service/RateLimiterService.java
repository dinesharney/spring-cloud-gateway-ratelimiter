package com.example.ratelimiter.service;

/**
 * Strategy interface to define the contract for various
 * rate limiting algorithm implementations.
 */
public interface RateLimiterService {
    boolean isAllowed(String key);
}