package com.kuma.cloud.reference.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("reference.executor")
public record BusinessExecutorProperties(@Min(1) int coreSize, @Min(1) int maxSize,
        @Min(1) int queueCapacity, @Min(1) int keepAliveSeconds, @Min(1) int shutdownSeconds) {
    public BusinessExecutorProperties {
        if (maxSize < coreSize) throw new IllegalArgumentException("业务线程池 max-size 不能小于 core-size");
    }
}
