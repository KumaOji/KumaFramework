package com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter;

import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity.RateLimiterRule;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.config.RateLimiterConfig;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.exception.SnowJeanException;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.observer.RateLimiterObserver;

/**
 * 简单工厂模式
 */
public class RateLimiterFactory {

    public static com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter.RateLimiter of(RateLimiterRule rule) {
        return of(rule, RateLimiterConfig.getInstance());
    }

    public static com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter.RateLimiter of(RateLimiterRule rule, RateLimiterConfig config) {
        switch (rule.getLimiterModel()) {
            case POINT: //本地限流
                com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter.RateLimiter limiterDefault = new com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter.RateLimiterDefault(rule, config);
                RateLimiterObserver.registered(limiterDefault, config);
                return limiterDefault;
            case CLOUD: //集群限流
                limiterDefault = new com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter.RateLimiterDefault(rule, config);
                rule.setName(rule.getName() == null ? String.valueOf(limiterDefault.hashCode()) : rule.getName());
                RateLimiterObserver.registered(limiterDefault, config);
                return limiterDefault;
            default:
                throw new SnowJeanException("CurrentModel Parameter not set");
        }
    }

}
