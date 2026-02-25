/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter;

import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity.RateLimiterRule;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.config.RateLimiterConfig;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.exception.SnowJeanException;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.observer.RateLimiterObserver;

public class RateLimiterFactory {
    public static RateLimiter of(RateLimiterRule rule) {
        return RateLimiterFactory.of(rule, RateLimiterConfig.getInstance());
    }

    public static RateLimiter of(RateLimiterRule rule, RateLimiterConfig config) {
        switch (rule.getLimiterModel()) {
            case POINT: {
                RateLimiterDefault limiterDefault = new RateLimiterDefault(rule, config);
                RateLimiterObserver.registered(limiterDefault, config);
                return limiterDefault;
            }
            case CLOUD: {
                RateLimiterDefault limiterDefault = new RateLimiterDefault(rule, config);
                rule.setName(rule.getName() == null ? String.valueOf(limiterDefault.hashCode()) : rule.getName());
                RateLimiterObserver.registered(limiterDefault, config);
                return limiterDefault;
            }
        }
        throw new SnowJeanException("CurrentModel Parameter not set");
    }
}

