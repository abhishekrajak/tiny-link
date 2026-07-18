package com.abhishek.github.tinylink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.rate-limiting")
@Data
public class EndpointLimitProperties {

    // Maps: Tier Name (e.g., BASE) -> (Endpoint Name -> Capacity Integer)
    private Map<String, Map<String, Integer>> tiers = new HashMap<>();

    private DefaultConfig defaultValue = new DefaultConfig();

    @Data
    public static class DefaultConfig {
        private int capacity = 30;
        private int durationMinutes = 60;
    }
}