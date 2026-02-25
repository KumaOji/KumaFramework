/*
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.ratelimit.ratelimitbs.spring;

import com.kuma.boot.ratelimit.ratelimitbs.spring.annotation.EnableRateLimit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@EnableRateLimit
@Configuration
@ConditionalOnClass(value={EnableRateLimit.class})
public class RateLimitAutoConfig {
}

