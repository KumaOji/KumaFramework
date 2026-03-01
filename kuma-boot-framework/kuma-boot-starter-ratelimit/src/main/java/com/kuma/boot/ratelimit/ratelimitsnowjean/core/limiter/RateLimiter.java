package com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter;

import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity.RateLimiterRule;
import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.client.MonitorService;

public interface RateLimiter {

    MonitorService getMonitorService();

    void init(RateLimiterRule rule);

    boolean tryAcquire();

    boolean tryAcquire(String s);

    String getId();

    RateLimiterRule getRule();
}
