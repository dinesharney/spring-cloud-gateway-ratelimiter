package com.example.ratelimiter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateLimitConfig {
    private String routeId;
    private String algorithm; // TokenBucket / LeakyBucket
    private int capacity;
    private int refillTokens;
    private int refillPeriodInSeconds;
}
