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
import com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitSlideWindowDto;
import com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitSlideWindowInfo;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import java.util.ArrayList;
import java.util.List;

public class RateLimitSlideWindow
extends AbstractRateLimit {
    private final int windowNum;

    public RateLimitSlideWindow(int windowNum) {
        this.windowNum = windowNum;
    }

    public RateLimitSlideWindow() {
        this(10);
    }

    @Override
    protected boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext rateLimitContext) {
        long limitCount;
        int permits;
        RateLimitSlideWindowInfo slideWindowInfo = this.queryCacheInfo(cacheKey, configDto, rateLimitContext);
        long oldSum = this.calcSum(slideWindowInfo, rateLimitContext);
        long countVal = oldSum + (long)(permits = configDto.getPermits());
        if (countVal > (limitCount = configDto.getCount().longValue())) {
            LogUtils.warn((String)"[RateLimit] countVal {} is gt than limit {}", (Object[])new Object[]{countVal, limitCount});
            return false;
        }
        long initTime = slideWindowInfo.getInitTime();
        long now = rateLimitContext.timer().time();
        long timeWindow = this.calcTimeWindow(configDto);
        int index = (int)((now - initTime) / timeWindow % (long)this.windowNum);
        List<RateLimitSlideWindowDto> windowDtoList = slideWindowInfo.getWindowList();
        RateLimitSlideWindowDto windowDto = windowDtoList.get(index);
        long oldExpireTime = windowDto.getExpireTime();
        if (now > oldExpireTime) {
            RateLimitSlideWindowDto newWindowDto = new RateLimitSlideWindowDto();
            newWindowDto.setExpireTime(now + timeWindow);
            newWindowDto.setCount(permits);
            windowDtoList.set(index, windowDto);
        } else {
            int newCount = windowDto.getCount() + permits;
            windowDto.setCount(newCount);
            windowDtoList.set(index, windowDto);
        }
        ICommonCacheService commonCacheService = rateLimitContext.cacheService();
        commonCacheService.set(cacheKey, JSON.toJSONString((Object)slideWindowInfo));
        return true;
    }

    private long calcSum(RateLimitSlideWindowInfo slideWindowInfo, IRateLimitContext rateLimitContext) {
        long sum = 0L;
        long now = rateLimitContext.timer().time();
        List<RateLimitSlideWindowDto> windowList = slideWindowInfo.getWindowList();
        for (RateLimitSlideWindowDto windowDto : windowList) {
            long expireTime;
            if (windowDto == null || now > (expireTime = windowDto.getExpireTime())) continue;
            long count = windowDto.getCount();
            sum += count;
        }
        return sum;
    }

    private RateLimitSlideWindowInfo queryCacheInfo(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext rateLimitContext) {
        ICommonCacheService cacheService = rateLimitContext.cacheService();
        String cacheValue = cacheService.get(cacheKey);
        if (StrUtil.isNotEmpty((CharSequence)cacheValue)) {
            return (RateLimitSlideWindowInfo)JSON.parseObject((String)cacheValue, RateLimitSlideWindowInfo.class);
        }
        long timeWindow = this.calcTimeWindow(configDto);
        ITimer timer = rateLimitContext.timer();
        long now = timer.time();
        ArrayList<RateLimitSlideWindowDto> windowList = new ArrayList<RateLimitSlideWindowDto>(this.windowNum);
        for (int i = 0; i < this.windowNum; ++i) {
            RateLimitSlideWindowDto windowDto = new RateLimitSlideWindowDto();
            windowDto.setCount(0);
            windowDto.setExpireTime(now + (long)i * timeWindow);
            windowList.add(windowDto);
        }
        RateLimitSlideWindowInfo windowInfo = new RateLimitSlideWindowInfo();
        windowInfo.setInitTime(now);
        windowInfo.setWindowList(windowList);
        return windowInfo;
    }

    private long calcTimeWindow(RateLimitConfigDto configDto) {
        long intervalMills = configDto.getTimeUnit().toMillis(configDto.getInterval());
        return intervalMills / (long)this.windowNum;
    }
}

