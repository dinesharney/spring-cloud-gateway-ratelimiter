package com.example.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller exposes simple test endpoints to validate
 * token bucket and leaky bucket rate limiting strategies.
 */
@RestController
@RequestMapping("/test")
public class RateLimiterTestController {

    @GetMapping("/tokenbucket")
    public String tokenBucketApi() {
        return "Response from Token Bucket protected API!";
    }

    @GetMapping("/leakybucket")
    public String leakyBucketApi() {
        return "Response from Leaky Bucket protected API!";
    }
}