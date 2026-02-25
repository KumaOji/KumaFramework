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
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class RateLimitSlideWindowQueue
extends AbstractRateLimit {
    @Override
    protected boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext context) {
        Queue<Long> queue = this.queryQueue(cacheKey, configDto, context);
        ICommonCacheService cacheService = context.cacheService();
        ITimer timer = context.timer();
        long now = timer.time();
        boolean offerResult = queue.offer(now);
        if (offerResult) {
            String cacheValue = JSON.toJSONString(queue);
            cacheService.set(cacheKey, cacheValue);
            return true;
        }
        long intervalInMills = configDto.getTimeUnit().toMillis(configDto.getInterval());
        Long headTimeInMills = queue.peek();
        long durationMills = now - headTimeInMills;
        if (durationMills > intervalInMills) {
            Long headTimeRemove = queue.poll();
            queue.offer(now);
            LogUtils.info((String)"Remove head value: {}, add new value: {}", (Object[])new Object[]{headTimeRemove, now});
            String cacheValue = JSON.toJSONString(queue);
            cacheService.set(cacheKey, cacheValue);
            return true;
        }
        return false;
    }

    private Queue<Long> queryQueue(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext context) {
        ICommonCacheService cacheService = context.cacheService();
        String cacheValue = cacheService.get(cacheKey);
        int count = configDto.getCount().intValue();
        ArrayBlockingQueue<Long> queue = new ArrayBlockingQueue<Long>(count);
        if (StrUtil.isNotEmpty((CharSequence)cacheValue)) {
            List list = JSON.parseArray((String)cacheValue, Long.class);
            queue.addAll(list);
            return queue;
        }
        return queue;
    }
}

