/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitbs.core.bs;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.core.constant.RateLimitConst;
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

    private RateLimitBs() {}

    /**
     * 新建对象实例
     *
     * @return this
     */
    public static RateLimitBs newInstance() {
        return new RateLimitBs();
    }

    /**
     * 限流算法
     */
    private IRateLimit rateLimit = RateLimits.tokenBucket();

    /**
     * 时间策略
     */
    private ITimer timer = Timers.system();

    /**
     * 缓存策略
     */
    private ICommonCacheService cacheService = new CommonCacheServiceMap();

    /**
     * 配置服务
     */
    private IRateLimitConfigService configService = new RateLimitConfigService();

    /**
     * 标识服务类
     */
    private IRateLimitTokenService tokenService = new RateLimitTokenService();

    /**
     * 方法标识策略
     */
    private IRateLimitMethodService methodService = new RateLimitMethodService();

    /**
     * 拒绝策略
     */
    private IRateLimitRejectListener rejectListener = new RateLimitRejectListenerException();

    /**
     * 对应的缓存 key 命名空间
     */
    private String cacheKeyNamespace = RateLimitConst.DEFAULT_CACHE_KEY_NAMESPACE;

    public RateLimitBs rateLimit(IRateLimit rateLimit) {
        ArgUtils.notNull(rateLimit, "rateLimit");

        this.rateLimit = rateLimit;
        return this;
    }

    public RateLimitBs timer(ITimer timer) {
        ArgUtils.notNull(timer, "timer");

        this.timer = timer;
        return this;
    }

    public RateLimitBs cacheService(ICommonCacheService cacheService) {
        ArgUtils.notNull(cacheService, "cacheService");

        this.cacheService = cacheService;
        return this;
    }

    public RateLimitBs configService(IRateLimitConfigService configService) {
        ArgUtils.notNull(configService, "configService");

        this.configService = configService;
        return this;
    }

    public RateLimitBs tokenService(IRateLimitTokenService tokenService) {
        ArgUtils.notNull(tokenService, "tokenService");

        this.tokenService = tokenService;
        return this;
    }

    public RateLimitBs methodService(IRateLimitMethodService methodService) {
        ArgUtils.notNull(methodService, "methodService");

        this.methodService = methodService;
        return this;
    }

    public RateLimitBs rejectListener(IRateLimitRejectListener rejectListener) {
        ArgUtils.notNull(rejectListener, "rejectListener");

        this.rejectListener = rejectListener;
        return this;
    }

    public RateLimitBs cacheKeyNamespace(String cacheKeyNamespace) {
        ArgUtils.notEmpty(cacheKeyNamespace, "cacheKeyNamespace");

        this.cacheKeyNamespace = cacheKeyNamespace;
        return this;
    }

    /**
     * 尝试获取锁
     *
     * @param method 方法
     * @param args   入参
     * @return 结果
     */
    public boolean tryAcquire(Method method, Object[] args) {
        ArgUtils.notNull(method, "method");

        IRateLimitContext rateLimitContext = RateLimitContext.newInstance()
                .method(method)
                .args(args)
                .timer(timer)
                .configService(configService)
                .tokenService(tokenService)
                .methodService(methodService)
                .rejectListener(rejectListener)
                .cacheService(cacheService)
                .cacheKeyNamespace(cacheKeyNamespace);

        return rateLimit.tryAcquire(rateLimitContext);
    }
}
