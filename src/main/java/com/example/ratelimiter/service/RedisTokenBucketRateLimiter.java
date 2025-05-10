package com.example.ratelimiter.service;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class RedisTokenBucketRateLimiter implements RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final int capacity;
    private final int refillTokens;
    private final int refillPeriodInSeconds;

    private static final String LUA_SCRIPT = """
        local tokens_key = KEYS[1]
        local timestamp_key = KEYS[2]

        local capacity = tonumber(ARGV[1])
        local refill_tokens = tonumber(ARGV[2])
        local refill_period = tonumber(ARGV[3])
        local now = tonumber(ARGV[4])

        local last_refill = tonumber(redis.call("GET", timestamp_key) or now)
        local tokens = tonumber(redis.call("GET", tokens_key) or capacity)

        local elapsed = now - last_refill
        if elapsed >= refill_period then
            tokens = math.min(capacity, tokens + (math.floor(elapsed / refill_period) * refill_tokens))
            last_refill = now
        end

        if tokens > 0 then
            tokens = tokens - 1
            redis.call("SET", tokens_key, tokens)
            redis.call("SET", timestamp_key, last_refill)
            return 1
        else
            return 0
        end
    """;

    public RedisTokenBucketRateLimiter(ReactiveStringRedisTemplate redisTemplate, int capacity, int refillTokens, int refillPeriodInSeconds) {
        this.redisTemplate = redisTemplate;
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodInSeconds = refillPeriodInSeconds;
    }

    @Override
    public boolean isAllowed(String key) {
        String tokenKey = "rate:tokens:" + key;
        String tsKey = "rate:ts:" + key;
        long now = System.currentTimeMillis() / 1000;

        RedisScript<Long> script = RedisScript.of(LUA_SCRIPT, Long.class);

        Mono<Long> result = redisTemplate.execute(
                script,
                Arrays.asList(tokenKey, tsKey),  // KEYS
                Arrays.asList(                   // ARGV
                        String.valueOf(capacity),
                        String.valueOf(refillTokens),
                        String.valueOf(refillPeriodInSeconds),
                        String.valueOf(now)
                )
        ).next();


        return result.blockOptional().orElse(0L) == 1;
    }
}
