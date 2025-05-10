package com.example.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the Spring Boot main application class responsible for bootstrapping
 * the Gateway Rate Limiter microservice.
 */
@SpringBootApplication
public class GatewayRateLimiterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayRateLimiterApplication.class, args);
    }
}