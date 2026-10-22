package com.kuma.boot.core.autoconfigure;

import java.time.Clock;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/** Shared, replaceable time source for application services and deterministic tests. */
@AutoConfiguration
public class ClockAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(Clock.class)
    public Clock clock() { return Clock.systemUTC(); }
}
