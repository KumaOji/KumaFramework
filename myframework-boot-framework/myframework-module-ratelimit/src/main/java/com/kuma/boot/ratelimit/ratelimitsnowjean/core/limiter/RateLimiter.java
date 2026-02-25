/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter;

import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity.RateLimiterRule;
import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.client.MonitorService;

public interface RateLimiter {
    public MonitorService getMonitorService();

    public void init(RateLimiterRule var1);

    public boolean tryAcquire();

    public boolean tryAcquire(String var1);

    public String getId();

    public RateLimiterRule getRule();
}

