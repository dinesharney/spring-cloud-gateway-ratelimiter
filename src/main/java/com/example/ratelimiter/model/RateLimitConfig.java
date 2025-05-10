package com.example.ratelimiter.model;

public class RateLimitConfig {
    private String routeId;
    private String algorithm;
    private int capacity;
    private int refillTokens;
    private int refillPeriodInSeconds;

    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getRefillTokens() { return refillTokens; }
    public void setRefillTokens(int refillTokens) { this.refillTokens = refillTokens; }

    public int getRefillPeriodInSeconds() { return refillPeriodInSeconds; }
    public void setRefillPeriodInSeconds(int refillPeriodInSeconds) { this.refillPeriodInSeconds = refillPeriodInSeconds; }
}
