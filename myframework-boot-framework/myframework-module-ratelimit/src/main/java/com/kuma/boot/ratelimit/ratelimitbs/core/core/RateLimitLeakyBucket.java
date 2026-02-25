/*
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.fastjson2.JSON
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimitContext;
import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;
import com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitLeakyBucketDto;
import com.kuma.boot.ratelimit.ratelimitbs.core.util.InnerRateLimitUtils;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;

public class RateLimitLeakyBucket
extends AbstractRateLimit {
    @Override
    public boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext context) {
        long capacity;
        long rate = InnerRateLimitUtils.calcRate(configDto);
        RateLimitLeakyBucketDto rateLimitTokenBucketDto = this.getRateLimitBucketDto(cacheKey, rate, context);
        int permits = configDto.getPermits();
        long water = this.calcWater(rateLimitTokenBucketDto, context, rate);
        long newWater = water + (long)permits;
        if (newWater <= (capacity = rateLimitTokenBucketDto.getCapacity())) {
            ITimer timer = context.timer();
            rateLimitTokenBucketDto.setWater(newWater);
            rateLimitTokenBucketDto.setLastUpdateTime(timer.time());
            ICommonCacheService commonCacheService = context.cacheService();
            commonCacheService.set(cacheKey, JSON.toJSONString((Object)rateLimitTokenBucketDto));
            return true;
        }
        LogUtils.info((String)"[RateLimit] leaky water is has been full!", (Object[])new Object[0]);
        return false;
    }

    private long calcWater(RateLimitLeakyBucketDto bucketDto, IRateLimitContext rateLimitContext, long rate) {
        long now = rateLimitContext.timer().time();
        long lastUpdateTime = bucketDto.getLastUpdateTime();
        long durationMs = now - lastUpdateTime;
        long leakyWater = (long)((double)durationMs * 1.0 * (double)rate / 1000.0);
        LogUtils.info((String)("[RateLimit] leaky water is " + leakyWater), (Object[])new Object[0]);
        long water = bucketDto.getWater();
        return Math.max(0L, water - leakyWater);
    }

    private RateLimitLeakyBucketDto getRateLimitBucketDto(String cacheKey, long rate, IRateLimitContext context) {
        ICommonCacheService commonCacheService = context.cacheService();
        ITimer timer = context.timer();
        String dtoJson = commonCacheService.get(cacheKey);
        RateLimitLeakyBucketDto bucketDto = null;
        if (StrUtil.isNotEmpty((CharSequence)dtoJson)) {
            bucketDto = (RateLimitLeakyBucketDto)JSON.parseObject((String)dtoJson, RateLimitLeakyBucketDto.class);
        } else {
            bucketDto = new RateLimitLeakyBucketDto();
            bucketDto.setRate(rate);
            bucketDto.setCapacity(rate * 8L);
            bucketDto.setWater(0L);
            bucketDto.setLastUpdateTime(timer.time());
        }
        return bucketDto;
    }
}

