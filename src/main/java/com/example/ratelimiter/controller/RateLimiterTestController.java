package com.example.ratelimiter.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class RateLimiterTestController {

    @GetMapping("/tokenbucket")
    @Operation(summary = "Token Bucket Protected API")
    public String tokenBucketApi() {
        return "Response from Token Bucket protected API!";
    }

    @GetMapping("/leakybucket")
    @Operation(summary = "Leaky Bucket Protected API")
    public String leakyBucketApi() {
        return "Response from Leaky Bucket protected API!";
    }
}
