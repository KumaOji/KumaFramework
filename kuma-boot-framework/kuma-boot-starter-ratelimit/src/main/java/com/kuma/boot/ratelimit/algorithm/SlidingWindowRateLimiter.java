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

package com.kuma.boot.ratelimit.algorithm;

import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import com.kuma.boot.common.utils.log.LogUtils;
/**
 * 为了缓解固定窗口的突发流量问题，可以采用滑动窗口算法，计算机网络中TCP的流量控制就是采用滑动窗口算法。
 *
 * 滑动窗口限流算法的原理是将一个大的时间窗口划分为多个小的时间窗口，每个小的窗口都有独立的计数。
 * 请求过来的时候，判断请求的次数是否超过整个窗口的限制。窗口的移动是每次向前滑动一个小的单元窗口。
 * 例如下面这个滑动窗口，将大时间窗口1min分成了5个小窗口，每个小窗口的时间是12s。
 * 每个单元格有自己独立的计数器，每过12s就会向前移动一格。
 * 假如有请求在00:01的时候过来，这时候窗口的计数就是3+12+9+15=39，也能起到限流的作用
 * 滑的格子越多，那么整体的滑动就会越平滑,限流的效果就会越精准。
 * 我们可以直接使用Redis的有序集合（zset）结构。
 *
 * 我们使用时间戳作为score和member，有请求过来的时候，就把当前时间戳添加到有序集合里。那么窗口之外的请求，我们可以根据窗口大小，计算出起始时间戳，删除窗口外的请求。这样，有序集合的大小，就是我们这个窗口的请求数了。
 *
 * 这里还有一个小的可以完善的点，zset在member相同的情况下，是会覆盖的，也就是说高并发情况下，时间戳可能会重复，那么就有可能统计的请求偏少，这里可以用时间戳+随机数来缓解，也可以生成唯一序列来解决，比如UUID、雪花算法等等。
 *
 * 用Redis实现了滑动窗口限流，解决了固定窗口限流的边界问题，当然这里也带来了新的问题，因为我们存储了窗口期的所有请求，所以高并发的情况下，可能会比较占内存。
 *
 */
public class SlidingWindowRateLimiter {
    public static final String KEY = "slidingWindowRateLimiter:";
    private RedissonClient redissonClient;
    /**
     * 请求次数限制
     */
    private Long limit;
    /**
     * 窗口大小（单位：S）
     */
    private Long windowSize;

    public SlidingWindowRateLimiter(Long limit, Long windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
    }

    public boolean triggerLimit(String path) {
        // 窗口计数
        RScoredSortedSet<Long> counter = redissonClient.getScoredSortedSet(KEY + path);
        // 使用分布式锁，避免并发设置初始值的时候，导致窗口计数被覆盖
        RLock rLock = redissonClient.getLock(KEY + "LOCK:" + path);
        try {
            rLock.lock(200, TimeUnit.MILLISECONDS);
            // 当前时间戳
            long currentTimestamp = System.currentTimeMillis();
            // 窗口起始时间戳
            long windowStartTimestamp = currentTimestamp - windowSize * 1000;
            // 移除窗口外的时间戳，左闭右开
            counter.removeRangeByScore(0, true, windowStartTimestamp, false);
            // 将当前时间戳作为score,也作为member，
            // TODO:高并发情况下可能没法保证唯一，可以加一个唯一标识
            counter.add(currentTimestamp, currentTimestamp);
            // 使用zset的元素个数，作为请求计数
            long count = counter.size();
            // 判断时间戳数量是否超过限流阈值
            if (count > limit) {
                LogUtils.info("[triggerLimit] path:" + path + " count:" + count + " over limit:" + limit);
                return true;
            }
            return false;
        } finally {
            rLock.unlock();
        }
    }
}
