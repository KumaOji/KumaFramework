/*
 *  org.springframework.context.annotation.EnableAspectJAutoProxy
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.ratelimit.ratelimitbs.spring.annotation;

import com.kuma.boot.ratelimit.ratelimitbs.spring.config.RateLimitConfig;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Import(value={RateLimitConfig.class})
@EnableAspectJAutoProxy
public @interface EnableRateLimit {
    public String value() default "rateLimit";

    public String timer() default "rateLimitTimer";

    public String cacheService() default "rateLimitCacheService";

    public String configService() default "rateLimitConfigService";

    public String tokenService() default "rateLimitTokenService";

    public String methodService() default "rateLimitMethodService";

    public String rejectListener() default "rateLimitRejectListener";

    public String cacheKeyNamespace() default "RATE_LIMIT";
}

