package com.kuma.cloud.reference.config;

import com.kuma.boot.core.autoconfigure.AsyncThreadPoolAutoConfiguration;
import java.time.Clock;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(AsyncThreadPoolAutoConfiguration.class)
public class ExecutionConfiguration {
    @Bean
    Clock clock() { return Clock.systemUTC(); }
}
