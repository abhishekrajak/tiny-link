package com.abhishek.github.tinylink.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.rate-limiting")
@Data
public class EndpointLimitProperties {

    // Spring maps any unique key under 'endpoints' directly into this map
    private Map<String, LimitConfig> endpoints = new HashMap<>();

    // Global fallback if an endpoint doesn't match any key in the map
    private LimitConfig defaultLimit = new LimitConfig(30L, 60L);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LimitConfig {
        private long capacity;
        private long durationMinutes;
    }
}