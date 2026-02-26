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

package com.kuma.boot.ratelimit.ratelimitbs.api.core;

import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;

import java.lang.reflect.Method;

/**
 * 限流核心上下文接口
 */
public interface IRateLimitContext {

    /**
     * 时间戳
     *
     * @return 时间戳
     */
    ITimer timer();

    /**
     * 配置服务类
     *
     * @return 服务类
     */
    IRateLimitConfigService configService();

    /**
     * 方法服务类
     *
     * @return 服务类
     */
    IRateLimitMethodService methodService();

    /**
     * 标识服务
     *
     * @return 服务
     */
    IRateLimitTokenService tokenService();

    /**
     * 缓存服务
     *
     * @return 统计服务
     */
    ICommonCacheService cacheService();

    /**
     * 拒绝时的监听策略
     *
     * @return 策略
     */
    IRateLimitRejectListener rejectListener();

    /**
     * 访问的方法
     *
     * @return 方法
     */
    Method method();

    /**
     * 访问的方法参数
     *
     * @return 方法参数
     */
    Object[] args();

    /**
     * 对应的缓存 key 命名空间
     *
     * @return 结果
     * @since 1.1.0
     */
    String cacheKeyNamespace();
}
