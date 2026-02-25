/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.api.core;

import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import java.lang.reflect.Method;

public interface IRateLimitContext {
    public ITimer timer();

    public IRateLimitConfigService configService();

    public IRateLimitMethodService methodService();

    public IRateLimitTokenService tokenService();

    public ICommonCacheService cacheService();

    public IRateLimitRejectListener rejectListener();

    public Method method();

    public Object[] args();

    public String cacheKeyNamespace();
}

