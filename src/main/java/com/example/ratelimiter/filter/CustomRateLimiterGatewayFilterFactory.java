package com.example.ratelimiter.filter;

import com.example.ratelimiter.service.LeakyBucketRateLimiter;
import com.example.ratelimiter.service.RateLimiterService;
import com.example.ratelimiter.service.TokenBucketRateLimiter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Gateway Filter Factory that routes requests through
 * the appropriate rate limiting algorithm based on route ID.
 */
@Component
public class CustomRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomRateLimiterGatewayFilterFactory.Config> {

    private final Map<String, RateLimiterService> rateLimiters = new ConcurrentHashMap<>();

    public CustomRateLimiterGatewayFilterFactory() {
        super(Config.class);
        rateLimiters.put("route1", new TokenBucketRateLimiter(5, 5, 60));
        rateLimiters.put("route2", new LeakyBucketRateLimiter(10, 1));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String routeId = config.getRouteId();
            RateLimiterService rateLimiter = rateLimiters.get(routeId);
            if (rateLimiter == null || rateLimiter.isAllowed(getClientKey(exchange))) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String getClientKey(ServerWebExchange exchange) {
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    public static class Config {
        private String routeId;
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
    }
}
