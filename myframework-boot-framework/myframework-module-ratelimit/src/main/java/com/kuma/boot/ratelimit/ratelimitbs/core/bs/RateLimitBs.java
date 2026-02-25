/*
 *  com.kuma.boot.common.utils.common.ArgUtils
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.bs;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.core.core.RateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.core.core.RateLimits;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.config.RateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.method.RateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.reject.RateLimitRejectListenerException;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.token.RateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.CommonCacheServiceMap;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.Timers;
import java.lang.reflect.Method;

public final class RateLimitBs {
    private IRateLimit rateLimit = RateLimits.tokenBucket();
    private ITimer timer = Timers.system();
    private ICommonCacheService cacheService = new CommonCacheServiceMap();
    private IRateLimitConfigService configService = new RateLimitConfigService();
    private IRateLimitTokenService tokenService = new RateLimitTokenService();
    private IRateLimitMethodService methodService = new RateLimitMethodService();
    private IRateLimitRejectListener rejectListener = new RateLimitRejectListenerException();
    private String cacheKeyNamespace = "RATE_LIMIT";

    private RateLimitBs() {
    }

    public static RateLimitBs newInstance() {
        return new RateLimitBs();
    }

    public RateLimitBs rateLimit(IRateLimit rateLimit) {
        ArgUtils.notNull((Object)rateLimit, (String)"rateLimit");
        this.rateLimit = rateLimit;
        return this;
    }

    public RateLimitBs timer(ITimer timer) {
        ArgUtils.notNull((Object)timer, (String)"timer");
        this.timer = timer;
        return this;
    }

    public RateLimitBs cacheService(ICommonCacheService cacheService) {
        ArgUtils.notNull((Object)cacheService, (String)"cacheService");
        this.cacheService = cacheService;
        return this;
    }

    public RateLimitBs configService(IRateLimitConfigService configService) {
        ArgUtils.notNull((Object)configService, (String)"configService");
        this.configService = configService;
        return this;
    }

    public RateLimitBs tokenService(IRateLimitTokenService tokenService) {
        ArgUtils.notNull((Object)tokenService, (String)"tokenService");
        this.tokenService = tokenService;
        return this;
    }

    public RateLimitBs methodService(IRateLimitMethodService methodService) {
        ArgUtils.notNull((Object)methodService, (String)"methodService");
        this.methodService = methodService;
        return this;
    }

    public RateLimitBs rejectListener(IRateLimitRejectListener rejectListener) {
        ArgUtils.notNull((Object)rejectListener, (String)"rejectListener");
        this.rejectListener = rejectListener;
        return this;
    }

    public RateLimitBs cacheKeyNamespace(String cacheKeyNamespace) {
        ArgUtils.notEmpty((String)cacheKeyNamespace, (String)"cacheKeyNamespace");
        this.cacheKeyNamespace = cacheKeyNamespace;
        return this;
    }

    public boolean tryAcquire(Method method, Object[] args) {
        ArgUtils.notNull((Object)method, (String)"method");
        RateLimitContext rateLimitContext = RateLimitContext.newInstance().method(method).args(args).timer(this.timer).configService(this.configService).tokenService(this.tokenService).methodService(this.methodService).rejectListener(this.rejectListener).cacheService(this.cacheService).cacheKeyNamespace(this.cacheKeyNamespace);
        return this.rateLimit.tryAcquire(rateLimitContext);
    }
}

