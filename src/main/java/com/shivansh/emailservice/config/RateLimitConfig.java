package com.shivansh.emailservice.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    private final CallerProperties callerProperties;

    @Value("${email.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${email.rate-limit.global-limit:500}")
    private int globalLimit;

    private final Map<String, Bucket> callerBuckets = new ConcurrentHashMap<>();
    private Bucket globalBucket;

    public RateLimitConfig(CallerProperties callerProperties) {
        this.callerProperties = callerProperties;
    }

    @PostConstruct
    public void init() {
        globalBucket = Bucket.builder()
                .addLimit(Bandwidth.classic(globalLimit,
                        Refill.intervally(globalLimit, Duration.ofHours(1))))
                .addLimit(Bandwidth.classic(20,
                        Refill.intervally(20, Duration.ofSeconds(1))))
                .build();
    }

    /**
     * Try to consume one token for the given caller.
     * Returns true if allowed, false if rate limited.
     */
    public boolean tryConsume(String callerName) {
        if (!enabled) {
            return true;
        }

        if (!globalBucket.tryConsume(1)) {
            return false;
        }

        Bucket bucket = callerBuckets.computeIfAbsent(callerName, name -> {
            int limit = callerProperties.getRateLimit(name);
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(limit,
                            Refill.intervally(limit, Duration.ofHours(1))))
                    .addLimit(Bandwidth.classic(10,
                            Refill.intervally(10, Duration.ofSeconds(1))))
                    .build();
        });

        return bucket.tryConsume(1);
    }

    /**
     * Estimate seconds until next token is available for the caller.
     */
    public long estimateRetryAfter(String callerName) {
        Bucket bucket = callerBuckets.get(callerName);
        if (bucket == null) {
            return 30;
        }
        long nanosToWait = bucket.estimateAbilityToConsume(1)
                .getNanosToWaitForRefill();
        long seconds = Duration.ofNanos(nanosToWait).toSeconds();
        return Math.max(seconds, 1);
    }
}
