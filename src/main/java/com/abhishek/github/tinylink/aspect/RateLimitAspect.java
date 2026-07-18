package com.abhishek.github.tinylink.aspect;

import com.abhishek.github.tinylink.annotation.RateLimited;
import com.abhishek.github.tinylink.config.EndpointLimitProperties;
import com.abhishek.github.tinylink.exception.RateLimitException;
import com.abhishek.github.tinylink.model.CustomUser;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Aspect
@Component
public class RateLimitAspect {

    private final ProxyManager<String> proxyManager;
    private final EndpointLimitProperties limitProperties;

    public RateLimitAspect(ProxyManager<String> proxyManager, EndpointLimitProperties limitProperties) {
        this.proxyManager = proxyManager;
        this.limitProperties = limitProperties;
    }

    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUser user)) {
            return joinPoint.proceed();
        }

        String userId = user.getUsername();

        EndpointLimitProperties.LimitConfig config = limitProperties.getEndpoints()
                .getOrDefault(rateLimited.policyKey(), limitProperties.getDefaultLimit());

        long capacity = config.getCapacity();
        long durationMinutes = config.getDurationMinutes();

        String methodName = joinPoint.getSignature().toShortString();
        String redisKey = "rate-limit:" + methodName + ":" + userId;

        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillGreedy(capacity, Duration.ofMinutes(durationMinutes))
                        .build())
                .build();

        Bucket bucket = proxyManager.builder().build(redisKey, configSupplier);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        if (probe.isConsumed()) {
            if (response != null) {
                response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            }
            return joinPoint.proceed();
        }

        long waitTimeSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
        if (response != null) {
            response.addHeader("Retry-After", String.valueOf(waitTimeSeconds));
        }

        throw new RateLimitException("Rate limit breached for policy field: " + rateLimited.policyKey());
    }
}