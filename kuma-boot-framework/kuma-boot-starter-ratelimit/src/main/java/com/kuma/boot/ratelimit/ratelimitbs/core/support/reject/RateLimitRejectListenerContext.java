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

public class RateLimitRejectListenerContext implements IRateLimitRejectListenerContext {

    public static RateLimitRejectListenerContext newInstance() {
        return new RateLimitRejectListenerContext();
    }

    /**
     * 时间戳
     */
    private ITimer timer;

    /**
     * 配置服务类
     * @since 1.0.0
     */
    private IRateLimitConfigService configService;

    /**
     * 方法服务类
     * @since 1.0.0
     */
    private IRateLimitMethodService methodService;

    /**
     * 标识服务
     * @since 1.0.0
     */
    private IRateLimitTokenService tokenService;

    /**
     * 缓存服务
     */
    private ICommonCacheService cacheService;

    /**
     * 拒绝时的监听策略
     * @since 1.0.0
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

    private String tokenId;

    private String methodId;

    private List<RateLimitConfigDto> configList;

    private boolean acquireFlag;

    /**
     * 缓存命名空间
     * @since 1.1.0
     */
    private String cacheKeyNamespace;

    @Override
    public ITimer timer() {
        return timer;
    }

    public RateLimitRejectListenerContext timer(ITimer timer) {
        this.timer = timer;
        return this;
    }

    @Override
    public IRateLimitConfigService configService() {
        return configService;
    }

    public RateLimitRejectListenerContext configService(IRateLimitConfigService configService) {
        this.configService = configService;
        return this;
    }

    @Override
    public IRateLimitMethodService methodService() {
        return methodService;
    }

    public RateLimitRejectListenerContext methodService(IRateLimitMethodService methodService) {
        this.methodService = methodService;
        return this;
    }

    @Override
    public IRateLimitTokenService tokenService() {
        return tokenService;
    }

    public RateLimitRejectListenerContext tokenService(IRateLimitTokenService tokenService) {
        this.tokenService = tokenService;
        return this;
    }

    @Override
    public ICommonCacheService cacheService() {
        return cacheService;
    }

    public RateLimitRejectListenerContext cacheService(ICommonCacheService cacheService) {
        this.cacheService = cacheService;
        return this;
    }

    @Override
    public IRateLimitRejectListener rejectListener() {
        return rejectListener;
    }

    public RateLimitRejectListenerContext rejectListener(IRateLimitRejectListener rejectListener) {
        this.rejectListener = rejectListener;
        return this;
    }

    @Override
    public Method method() {
        return method;
    }

    public RateLimitRejectListenerContext method(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public Object[] args() {
        return args;
    }

    public RateLimitRejectListenerContext args(Object[] args) {
        this.args = args;
        return this;
    }

    @Override
    public String tokenId() {
        return tokenId;
    }

    public RateLimitRejectListenerContext tokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    @Override
    public String methodId() {
        return methodId;
    }

    public RateLimitRejectListenerContext methodId(String methodId) {
        this.methodId = methodId;
        return this;
    }

    @Override
    public List<RateLimitConfigDto> configList() {
        return configList;
    }

    public RateLimitRejectListenerContext configList(List<RateLimitConfigDto> configList) {
        this.configList = configList;
        return this;
    }

    @Override
    public boolean acquireFlag() {
        return acquireFlag;
    }

    public RateLimitRejectListenerContext acquireFlag(boolean acquireFlag) {
        this.acquireFlag = acquireFlag;
        return this;
    }

    @Override
    public String cacheKeyNamespace() {
        return cacheKeyNamespace;
    }

    public RateLimitRejectListenerContext cacheKeyNamespace(String cacheKeyNamespace) {
        this.cacheKeyNamespace = cacheKeyNamespace;
        return this;
    }

    @Override
    public String toString() {
        return "RateLimitRejectListenerContext{" + "timer="
                + timer + ", configService="
                + configService + ", methodService="
                + methodService + ", tokenService="
                + tokenService + ", cacheService="
                + cacheService + ", rejectListener="
                + rejectListener + ", method="
                + method + ", args="
                + Arrays.toString(args) + ", tokenId='"
                + tokenId + '\'' + ", methodId='"
                + methodId + '\'' + ", configList="
                + configList + ", acquireFlag="
                + acquireFlag + ", cacheKeyNamespace='"
                + cacheKeyNamespace + '\'' + '}';
    }
}
