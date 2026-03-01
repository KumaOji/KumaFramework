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
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 滑动窗口限制次数
 * <p>
 * 1. 限制 queue 的大小与 count 一致
 * 2. 队首和队尾时间对比
 * <p>
 * 这个和最正宗的滑动窗口有些区别。
 */
public class RateLimitSlideWindowQueue extends com.kuma.boot.ratelimit.ratelimitbs.core.core.AbstractRateLimit {

    @Override
    protected boolean doAcquire(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext context) {
        // 队列？
        Queue<Long> queue = queryQueue(cacheKey, configDto, context);

        // 1. 将时间放入队列中 如果放得下，直接可以执行。反之，需要等待
        // 2. 等待完成之后，将第一个元素剔除。将最新的时间加入队列中。
        final ICommonCacheService cacheService = context.cacheService();
        final ITimer timer = context.timer();
        long now = timer.time();

        // 2.1 直接放入成功
        boolean offerResult = queue.offer(now);
        if (offerResult) {
            String cacheValue = JSON.toJSONString(queue);
            cacheService.set(cacheKey, cacheValue);
            return true;
        }

        // 2.2 直接放入失败
        // 2.2.1 取出头节点，获取最初的时间
        long intervalInMills = configDto.getTimeUnit().toMillis(configDto.getInterval());
        Long headTimeInMills = queue.peek();
        long durationMills = now - headTimeInMills;
        if (durationMills > intervalInMills) {
            Long headTimeRemove = queue.poll();
            queue.offer(now);

            LogUtils.info("Remove head value: {}, add new value: {}", headTimeRemove, now);

            String cacheValue = JSON.toJSONString(queue);
            cacheService.set(cacheKey, cacheValue);
            return true;
        }

        return false;
    }

    private Queue<Long> queryQueue(String cacheKey, RateLimitConfigDto configDto, IRateLimitContext context) {
        final ICommonCacheService cacheService = context.cacheService();
        String cacheValue = cacheService.get(cacheKey);

        int count = configDto.getCount().intValue();
        Queue<Long> queue = new ArrayBlockingQueue<>(count);

        if (StrUtil.isNotEmpty(cacheValue)) {
            List<Long> list = JSON.parseArray(cacheValue, Long.class);
            queue.addAll(list);
            return queue;
        } else {
            return queue;
        }
    }
}
