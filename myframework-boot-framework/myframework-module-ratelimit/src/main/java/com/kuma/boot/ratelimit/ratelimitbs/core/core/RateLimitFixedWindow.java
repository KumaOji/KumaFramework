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

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import cn.hutool.core.util.StrUtil;

/**
 * 固定时间窗口
 */
public class RateLimitFixedWindow extends com.kuma.boot.ratelimit.ratelimitbs.core.core.AbstractRateLimit {

    /**
     * 固定窗口的实现方式比较简单，直接设置过期时间即可。
     *
     * @param cacheKey 缓存标识
     * @param configDto 配置
     * @param rateLimitContext 上下文
     * @return 结果
     */
    @Override
    protected boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext rateLimitContext) {
        final ICommonCacheService cacheService = rateLimitContext.cacheService();
        final int permits = configDto.getPermits();

        String cacheValue = cacheService.get(cacheKey);
        if (StrUtil.isEmpty(cacheKey)) {
            final long expireMills = configDto.getTimeUnit().toMillis(configDto.getInterval());
            LogUtils.info("cacheKey: {} 对应的历史配置为空，进行初始化");
            // 模式初始化为0次
            cacheValue = "0";
            cacheService.set(cacheKey, cacheValue, expireMills);
        }

        long cacheCount = Long.parseLong(cacheValue);

        long newCount = cacheCount + permits;
        final long configCount = configDto.getCount();
        if (newCount > configCount) {
            LogUtils.warn("newCount {} 大于配置的 {}", newCount, configCount);
            return false;
        } else {
            long ttlMills = cacheService.ttl(cacheKey);
            if (ttlMills > 0) {
                // 直接 set 一个值，redis 会将其有效期设置为永远。
                cacheService.set(cacheKey, cacheValue, ttlMills);
            }

            return true;
        }
    }
}
