/*
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RLock
 *  org.redisson.api.RScoredSortedSet
 *  org.redisson.api.RedissonClient
 */
package com.kuma.boot.ratelimit.algorithm;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

public class SlidingWindowRateLimiter {
    public static final String KEY = "slidingWindowRateLimiter:";
    private RedissonClient redissonClient;
    private Long limit;
    private Long windowSize;

    public SlidingWindowRateLimiter(Long limit, Long windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean triggerLimit(String path) {
        RScoredSortedSet counter = this.redissonClient.getScoredSortedSet(KEY + path);
        RLock rLock = this.redissonClient.getLock("slidingWindowRateLimiter:LOCK:" + path);
        try {
            rLock.lock(200L, TimeUnit.MILLISECONDS);
            long currentTimestamp = System.currentTimeMillis();
            long windowStartTimestamp = currentTimestamp - this.windowSize * 1000L;
            counter.removeRangeByScore(0.0, true, (double)windowStartTimestamp, false);
            counter.add((double)currentTimestamp, (Object)currentTimestamp);
            long count = counter.size();
            if (count > this.limit) {
                LogUtils.info((String)("[triggerLimit] path:" + path + " count:" + count + " over limit:" + this.limit), (Object[])new Object[0]);
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            rLock.unlock();
        }
    }
}

