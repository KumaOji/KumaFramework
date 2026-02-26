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

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitLeakyBucketDto;
import com.kuma.boot.ratelimit.ratelimitbs.core.util.InnerRateLimitUtils;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import cn.hutool.core.util.StrUtil;

/**
 * 漏桶算法
 */
public class RateLimitLeakyBucket extends com.kuma.boot.ratelimit.ratelimitbs.core.core.AbstractRateLimit {

    /**
     * 尝试获取锁
     *
     * @param cacheKey  缓存标识
     * @param configDto 配置对象
     * @param context   上下文
     * @return 结果
     */
    @Override
    public boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext context) {
        final long rate = InnerRateLimitUtils.calcRate(configDto);
        RateLimitLeakyBucketDto rateLimitTokenBucketDto = getRateLimitBucketDto(cacheKey, rate, context);

        int permits = configDto.getPermits();
        long water = calcWater(rateLimitTokenBucketDto, context, rate);
        final long capacity = rateLimitTokenBucketDto.getCapacity();
        final long newWater = water + permits;
        if (newWater <= capacity) {
            final ITimer timer = context.timer();

            // 尝试加水,并且水还未满
            rateLimitTokenBucketDto.setWater(newWater);
            rateLimitTokenBucketDto.setLastUpdateTime(timer.time());

            final ICommonCacheService commonCacheService = context.cacheService();
            commonCacheService.set(cacheKey, JSON.toJSONString(rateLimitTokenBucketDto));
            return true;
        } else {
            // 水满，拒绝加水
            LogUtils.info("[RateLimit] leaky water is has been full!");
            return false;
        }
    }

    /**
     * 首先计算一次数量
     *
     * @param bucketDto        信息
     * @param rateLimitContext 上下文
     * @param rate             速率
     * @return 结果
     */
    private long calcWater(RateLimitLeakyBucketDto bucketDto, IRateLimitContext rateLimitContext, final long rate) {
        long now = rateLimitContext.timer().time();
        long lastUpdateTime = bucketDto.getLastUpdateTime();
        // 先执行漏水，计算剩余水量
        long durationMs = now - lastUpdateTime;
        long leakyWater = (long) (durationMs * 1.0 * rate / 1000);
        LogUtils.info("[RateLimit] leaky water is " + leakyWater);
        long water = bucketDto.getWater();

        // 确保最小为 0
        return Math.max(0, water - leakyWater);
    }

    /**
     * 获取对应的配置信息
     *
     * @param cacheKey 缓存 key
     * @param rate     速率
     * @param context  上下文
     * @return 结果
     */
    private RateLimitLeakyBucketDto getRateLimitBucketDto(String cacheKey, long rate, IRateLimitContext context) {
        final ICommonCacheService commonCacheService = context.cacheService();
        final ITimer timer = context.timer();

        String dtoJson = commonCacheService.get(cacheKey);
        RateLimitLeakyBucketDto bucketDto = null;
        if (StrUtil.isNotEmpty(dtoJson)) {
            bucketDto = JSON.parseObject(dtoJson, RateLimitLeakyBucketDto.class);
        } else {
            // 初始化
            bucketDto = new RateLimitLeakyBucketDto();
            bucketDto.setRate(rate);
            bucketDto.setCapacity(rate * 8);

            // 水量初始化为 0
            bucketDto.setWater(0);
            bucketDto.setLastUpdateTime(timer.time());
        }

        return bucketDto;
    }
}
