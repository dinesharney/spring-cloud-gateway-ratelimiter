package com.example.ratelimiter.filter;

import com.example.ratelimiter.model.RateLimitConfig;
import com.example.ratelimiter.service.LeakyBucketRateLimiter;
import com.example.ratelimiter.service.RateLimiterService;
import com.example.ratelimiter.service.TokenBucketRateLimiter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CustomRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomRateLimiterGatewayFilterFactory.Config> {

    private final Map<String, RateLimiterService> rateLimiters = new ConcurrentHashMap<>();

    public CustomRateLimiterGatewayFilterFactory() {
        super(Config.class);
        // Hardcoded configurations for now (dynamic loading later)
        initializeRateLimiters();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String routeId = config.getRouteId();

            RateLimiterService rateLimiter = rateLimiters.get(routeId);
            if (rateLimiter == null) {
                return chain.filter(exchange);
            }

            if (rateLimiter.isAllowed(getClientKey(exchange))) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String getClientKey(ServerWebExchange exchange) {
        // Use IP address or Authorization token etc
        return exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();
    }

    private void initializeRateLimiters() {
        // Here you could load from external config later (Redis, DB, etc)
        rateLimiters.put("route1", new TokenBucketRateLimiter(5, 5, 60)); // 5 requests/minute
        rateLimiters.put("route2", new LeakyBucketRateLimiter(10, 1)); // 1 leak/second
    }

    @Data
    public static class Config {
        private String routeId;
    }
}
