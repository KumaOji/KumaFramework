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
import com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitTokenBucketDto;
import com.kuma.boot.ratelimit.ratelimitbs.core.util.InnerRateLimitUtils;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;

public class RateLimitTokenBucket
extends AbstractRateLimit {
    @Override
    public boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext context) {
        long rate = InnerRateLimitUtils.calcRate(configDto);
        RateLimitTokenBucketDto rateLimitTokenBucketDto = this.getRateLimitBucketDto(cacheKey, rate, context);
        ICommonCacheService commonCacheService = context.cacheService();
        ITimer timer = context.timer();
        int permits = configDto.getPermits();
        long tokenNum = rateLimitTokenBucketDto.getTokenNum();
        if (tokenNum < (long)permits) {
            long now = timer.time();
            long durationMs = now - rateLimitTokenBucketDto.getLastUpdateTime();
            long addTokenNum = (long)((double)durationMs * 1.0 * (double)rate / 1000.0);
            LogUtils.debug((String)("[Limit] add token is " + addTokenNum), (Object[])new Object[0]);
            long newTokenNum = Math.min(addTokenNum + tokenNum, rateLimitTokenBucketDto.getCapacity());
            if (newTokenNum >= (long)permits) {
                rateLimitTokenBucketDto.setLastUpdateTime(now);
                rateLimitTokenBucketDto.setTokenNum(newTokenNum - (long)permits);
                commonCacheService.set(cacheKey, JSON.toJSONString((Object)rateLimitTokenBucketDto));
                return true;
            }
            return false;
        }
        rateLimitTokenBucketDto.setTokenNum(tokenNum - (long)permits);
        commonCacheService.set(cacheKey, JSON.toJSONString((Object)rateLimitTokenBucketDto));
        return true;
    }

    private RateLimitTokenBucketDto getRateLimitBucketDto(String cacheKey, long rate, IRateLimitContext context) {
        ICommonCacheService commonCacheService = context.cacheService();
        ITimer timer = context.timer();
        String dtoJson = commonCacheService.get(cacheKey);
        RateLimitTokenBucketDto bucketDto = null;
        if (StrUtil.isNotEmpty((CharSequence)dtoJson)) {
            bucketDto = (RateLimitTokenBucketDto)JSON.parseObject((String)dtoJson, RateLimitTokenBucketDto.class);
        } else {
            bucketDto = new RateLimitTokenBucketDto();
            bucketDto.setRate(rate);
            bucketDto.setCapacity(rate * 8L);
            bucketDto.setTokenNum(1L);
            bucketDto.setLastUpdateTime(timer.time());
        }
        return bucketDto;
    }
}

