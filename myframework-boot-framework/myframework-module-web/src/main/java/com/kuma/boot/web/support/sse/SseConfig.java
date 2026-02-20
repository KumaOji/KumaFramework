/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.DependsOn
 */
package com.kuma.boot.web.support.sse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

public class SseConfig {
    @Bean
    @ConditionalOnProperty(prefix="zebra.web.sse", name={"enabled"}, havingValue="true")
    public SseEmitterService sseEmitterService() {
        return new SseEmitterService();
    }

    @Bean
    @DependsOn(value={"sseEmitterService"})
    @ConditionalOnProperty(prefix="zebra.web.sse", name={"enabled"}, havingValue="true")
    public SseEmitterController sseEmitterController() {
        return new SseEmitterController();
    }
}

