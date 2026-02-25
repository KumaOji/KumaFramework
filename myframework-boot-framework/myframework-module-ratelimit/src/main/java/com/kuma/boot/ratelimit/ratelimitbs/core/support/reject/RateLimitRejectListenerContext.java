/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.reject;

import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListenerContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class RateLimitRejectListenerContext
implements IRateLimitRejectListenerContext {
    private ITimer timer;
    private IRateLimitConfigService configService;
    private IRateLimitMethodService methodService;
    private IRateLimitTokenService tokenService;
    private ICommonCacheService cacheService;
    private IRateLimitRejectListener rejectListener;
    private Method method;
    private Object[] args;
    private String tokenId;
    private String methodId;
    private List<RateLimitConfigDto> configList;
    private boolean acquireFlag;
    private String cacheKeyNamespace;

    public static RateLimitRejectListenerContext newInstance() {
        return new RateLimitRejectListenerContext();
    }

    @Override
    public ITimer timer() {
        return this.timer;
    }

    public RateLimitRejectListenerContext timer(ITimer timer) {
        this.timer = timer;
        return this;
    }

    @Override
    public IRateLimitConfigService configService() {
        return this.configService;
    }

    public RateLimitRejectListenerContext configService(IRateLimitConfigService configService) {
        this.configService = configService;
        return this;
    }

    @Override
    public IRateLimitMethodService methodService() {
        return this.methodService;
    }

    public RateLimitRejectListenerContext methodService(IRateLimitMethodService methodService) {
        this.methodService = methodService;
        return this;
    }

    @Override
    public IRateLimitTokenService tokenService() {
        return this.tokenService;
    }

    public RateLimitRejectListenerContext tokenService(IRateLimitTokenService tokenService) {
        this.tokenService = tokenService;
        return this;
    }

    @Override
    public ICommonCacheService cacheService() {
        return this.cacheService;
    }

    public RateLimitRejectListenerContext cacheService(ICommonCacheService cacheService) {
        this.cacheService = cacheService;
        return this;
    }

    @Override
    public IRateLimitRejectListener rejectListener() {
        return this.rejectListener;
    }

    public RateLimitRejectListenerContext rejectListener(IRateLimitRejectListener rejectListener) {
        this.rejectListener = rejectListener;
        return this;
    }

    @Override
    public Method method() {
        return this.method;
    }

    public RateLimitRejectListenerContext method(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public Object[] args() {
        return this.args;
    }

    public RateLimitRejectListenerContext args(Object[] args) {
        this.args = args;
        return this;
    }

    @Override
    public String tokenId() {
        return this.tokenId;
    }

    public RateLimitRejectListenerContext tokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    @Override
    public String methodId() {
        return this.methodId;
    }

    public RateLimitRejectListenerContext methodId(String methodId) {
        this.methodId = methodId;
        return this;
    }

    @Override
    public List<RateLimitConfigDto> configList() {
        return this.configList;
    }

    public RateLimitRejectListenerContext configList(List<RateLimitConfigDto> configList) {
        this.configList = configList;
        return this;
    }

    @Override
    public boolean acquireFlag() {
        return this.acquireFlag;
    }

    public RateLimitRejectListenerContext acquireFlag(boolean acquireFlag) {
        this.acquireFlag = acquireFlag;
        return this;
    }

    @Override
    public String cacheKeyNamespace() {
        return this.cacheKeyNamespace;
    }

    public RateLimitRejectListenerContext cacheKeyNamespace(String cacheKeyNamespace) {
        this.cacheKeyNamespace = cacheKeyNamespace;
        return this;
    }

    public String toString() {
        return "RateLimitRejectListenerContext{timer=" + String.valueOf(this.timer) + ", configService=" + String.valueOf(this.configService) + ", methodService=" + String.valueOf(this.methodService) + ", tokenService=" + String.valueOf(this.tokenService) + ", cacheService=" + String.valueOf(this.cacheService) + ", rejectListener=" + String.valueOf(this.rejectListener) + ", method=" + String.valueOf(this.method) + ", args=" + Arrays.toString(this.args) + ", tokenId='" + this.tokenId + "', methodId='" + this.methodId + "', configList=" + String.valueOf(this.configList) + ", acquireFlag=" + this.acquireFlag + ", cacheKeyNamespace='" + this.cacheKeyNamespace + "'}";
    }
}

