/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import java.lang.reflect.Method;

public class RateLimitContext
implements IRateLimitContext {
    private ITimer timer;
    private IRateLimitConfigService configService;
    private IRateLimitMethodService methodService;
    private IRateLimitTokenService tokenService;
    private ICommonCacheService cacheService;
    private IRateLimitRejectListener rejectListener;
    private Method method;
    private Object[] args;
    private String cacheKeyNamespace;

    public static RateLimitContext newInstance() {
        return new RateLimitContext();
    }

    @Override
    public ITimer timer() {
        return this.timer;
    }

    public RateLimitContext timer(ITimer timer) {
        this.timer = timer;
        return this;
    }

    @Override
    public IRateLimitConfigService configService() {
        return this.configService;
    }

    public RateLimitContext configService(IRateLimitConfigService configService) {
        this.configService = configService;
        return this;
    }

    @Override
    public IRateLimitMethodService methodService() {
        return this.methodService;
    }

    public RateLimitContext methodService(IRateLimitMethodService methodService) {
        this.methodService = methodService;
        return this;
    }

    @Override
    public IRateLimitTokenService tokenService() {
        return this.tokenService;
    }

    public RateLimitContext tokenService(IRateLimitTokenService tokenService) {
        this.tokenService = tokenService;
        return this;
    }

    @Override
    public IRateLimitRejectListener rejectListener() {
        return this.rejectListener;
    }

    public RateLimitContext rejectListener(IRateLimitRejectListener rejectListener) {
        this.rejectListener = rejectListener;
        return this;
    }

    @Override
    public Method method() {
        return this.method;
    }

    public RateLimitContext method(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public Object[] args() {
        return this.args;
    }

    public RateLimitContext args(Object[] args) {
        this.args = args;
        return this;
    }

    @Override
    public ICommonCacheService cacheService() {
        return this.cacheService;
    }

    public RateLimitContext cacheService(ICommonCacheService cacheService) {
        this.cacheService = cacheService;
        return this;
    }

    @Override
    public String cacheKeyNamespace() {
        return this.cacheKeyNamespace;
    }

    public RateLimitContext cacheKeyNamespace(String cacheKeyNamespace) {
        this.cacheKeyNamespace = cacheKeyNamespace;
        return this;
    }
}

