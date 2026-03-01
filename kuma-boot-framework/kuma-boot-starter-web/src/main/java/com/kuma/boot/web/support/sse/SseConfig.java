package com.kuma.boot.web.support.sse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

public class SseConfig {
    @Bean
    @ConditionalOnProperty(prefix = "zebra.web.sse", name = "enabled", havingValue = "true")
    public SseEmitterService sseEmitterService() {
        return new SseEmitterService();
    }

    @Bean
    @DependsOn("sseEmitterService")
    @ConditionalOnProperty(prefix = "zebra.web.sse", name = "enabled", havingValue = "true")
    public SseEmitterController sseEmitterController() {
        return new SseEmitterController();
    }
}
