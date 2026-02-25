/*
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.ComponentScan
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.config;

import com.kuma.boot.ratelimit.ratelimitsnowjean.config.annotation.aspect.RateLimiterAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SnowJeanAutoConfiguration {
    @Bean
    public RateLimiterAspect rateLimiterAspect() {
        return new RateLimiterAspect();
    }
}

