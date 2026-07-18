package com.abhishek.github.tinylink.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;

import java.time.Duration;

public enum UserTier {
    PREMIUM(100, Duration.ofMinutes(1)),   // 100 requests per minute
    STANDARD(20, Duration.ofMinutes(1)),   // 20 requests per minute
    GUEST(5, Duration.ofMinutes(1));       // 5 requests per minute

    private final long capacity;
    private final Duration period;

    UserTier(long capacity, Duration period) {
        this.capacity = capacity;
        this.period = period;
    }

    public BucketConfiguration getConfiguration() {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillGreedy(capacity, period)
                        .build())
                .build();
    }

    public static UserTier fromString(String status) {
        try {
            return UserTier.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return GUEST; // Fail-safe default
        }
    }
}