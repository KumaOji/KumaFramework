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

package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;

import java.lang.reflect.Method;

public class RateLimitContext implements IRateLimitContext {

    /**
     * 时间戳
     */
    private ITimer timer;

    /**
     * 配置服务类
     */
    private IRateLimitConfigService configService;

    /**
     * 方法服务类
     */
    private IRateLimitMethodService methodService;

    /**
     * 标识服务
     */
    private IRateLimitTokenService tokenService;

    /**
     * 缓存服务
     */
    private ICommonCacheService cacheService;

    /**
     * 拒绝时的监听策略
     */
    private IRateLimitRejectListener rejectListener;

    /**
     * 访问的方法
     */
    private Method method;

    /**
     * 访问的方法参数
     */
    private Object[] args;

    /**
     * 命名空间
     *
     * @since 1.1.0
     */
    private String cacheKeyNamespace;

    public static RateLimitContext newInstance() {
        return new RateLimitContext();
    }

    @Override
    public ITimer timer() {
        return timer;
    }

    public RateLimitContext timer(ITimer timer) {
        this.timer = timer;
        return this;
    }

    @Override
    public IRateLimitConfigService configService() {
        return configService;
    }

    public RateLimitContext configService(IRateLimitConfigService configService) {
        this.configService = configService;
        return this;
    }

    @Override
    public IRateLimitMethodService methodService() {
        return methodService;
    }

    public RateLimitContext methodService(IRateLimitMethodService methodService) {
        this.methodService = methodService;
        return this;
    }

    @Override
    public IRateLimitTokenService tokenService() {
        return tokenService;
    }

    public RateLimitContext tokenService(IRateLimitTokenService tokenService) {
        this.tokenService = tokenService;
        return this;
    }

    @Override
    public IRateLimitRejectListener rejectListener() {
        return rejectListener;
    }

    public RateLimitContext rejectListener(IRateLimitRejectListener rejectListener) {
        this.rejectListener = rejectListener;
        return this;
    }

    @Override
    public Method method() {
        return method;
    }

    public RateLimitContext method(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public Object[] args() {
        return args;
    }

    public RateLimitContext args(Object[] args) {
        this.args = args;
        return this;
    }

    @Override
    public ICommonCacheService cacheService() {
        return cacheService;
    }

    public RateLimitContext cacheService(ICommonCacheService cacheService) {
        this.cacheService = cacheService;
        return this;
    }

    @Override
    public String cacheKeyNamespace() {
        return cacheKeyNamespace;
    }

    public RateLimitContext cacheKeyNamespace(String cacheKeyNamespace) {
        this.cacheKeyNamespace = cacheKeyNamespace;
        return this;
    }
}
