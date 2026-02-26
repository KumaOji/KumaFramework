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
import com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitTokenBucketDto;
import com.kuma.boot.ratelimit.ratelimitbs.core.util.InnerRateLimitUtils;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import cn.hutool.core.util.StrUtil;

/**
 * 令牌桶算法
 */
public class RateLimitTokenBucket extends com.kuma.boot.ratelimit.ratelimitbs.core.core.AbstractRateLimit {

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
        RateLimitTokenBucketDto rateLimitTokenBucketDto = getRateLimitBucketDto(cacheKey, rate, context);
        final ICommonCacheService commonCacheService = context.cacheService();
        final ITimer timer = context.timer();

        int permits = configDto.getPermits();
        long tokenNum = rateLimitTokenBucketDto.getTokenNum();
        if (tokenNum < permits) {
            // 加入令牌
            long now = timer.time();
            long durationMs = now - rateLimitTokenBucketDto.getLastUpdateTime();
            // 增量部分
            long addTokenNum = (long) (durationMs * 1.0 * rate / 1000);
            LogUtils.debug("[Limit] add token is " + addTokenNum);

            // 新的令牌数量，丢弃超出的部分
            long newTokenNum = Math.min(addTokenNum + tokenNum, rateLimitTokenBucketDto.getCapacity());
            if (newTokenNum >= permits) {
                rateLimitTokenBucketDto.setLastUpdateTime(now);
                rateLimitTokenBucketDto.setTokenNum(newTokenNum - permits);
                commonCacheService.set(cacheKey, JSON.toJSONString(rateLimitTokenBucketDto));
                return true;
            } else {
                // 时间不够
                return false;
            }
        } else {
            // 正常够使用的场景
            rateLimitTokenBucketDto.setTokenNum(tokenNum - permits);
            commonCacheService.set(cacheKey, JSON.toJSONString(rateLimitTokenBucketDto));
        }

        return true;
    }

    /**
     * 获取对应的配置信息
     *
     * @param cacheKey 缓存 key
     * @param rate     速率
     * @param context  上下文
     * @return 结果
     */
    private RateLimitTokenBucketDto getRateLimitBucketDto(String cacheKey, long rate, IRateLimitContext context) {
        final ICommonCacheService commonCacheService = context.cacheService();
        final ITimer timer = context.timer();

        String dtoJson = commonCacheService.get(cacheKey);
        RateLimitTokenBucketDto bucketDto = null;
        if (StrUtil.isNotEmpty(dtoJson)) {
            bucketDto = JSON.parseObject(dtoJson, RateLimitTokenBucketDto.class);
        } else {
            // 初始化
            bucketDto = new RateLimitTokenBucketDto();
            bucketDto.setRate(rate);
            bucketDto.setCapacity(rate * 8);
            // 默认初始化为 1，应该比较合理一点
            // 如果是0，可能导致一开始无法访问
            bucketDto.setTokenNum(1);
            bucketDto.setLastUpdateTime(timer.time());
        }

        return bucketDto;
    }
}
