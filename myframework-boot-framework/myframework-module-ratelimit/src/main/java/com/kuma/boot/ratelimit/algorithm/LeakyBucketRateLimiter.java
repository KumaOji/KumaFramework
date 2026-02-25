/*
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RLock
 *  org.redisson.api.RScoredSortedSet
 *  org.redisson.api.RSet
 *  org.redisson.api.RedissonClient
 */
package com.kuma.boot.ratelimit.algorithm;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

public class LeakyBucketRateLimiter {
    private RedissonClient redissonClient;
    private static final String KEY_PREFIX = "LeakyBucket:";
    private Long bucketSize;
    private Long leakRate;

    public LeakyBucketRateLimiter(Long bucketSize, Long leakRate) {
        this.bucketSize = bucketSize;
        this.leakRate = leakRate;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::leakWater, 0L, 1L, TimeUnit.SECONDS);
    }

    public void leakWater() {
        RSet pathSet = this.redissonClient.getSet("LeakyBucket::pathSet");
        for (String path : pathSet) {
            String redisKey = KEY_PREFIX + path;
            RScoredSortedSet bucket = this.redissonClient.getScoredSortedSet(KEY_PREFIX + path);
            long now = System.currentTimeMillis();
            bucket.removeRangeByScore(0.0, true, (double)(now - 1000L * this.leakRate), true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean triggerLimit(String path) {
        RLock rLock = this.redissonClient.getLock("LeakyBucket:LOCK:" + path);
        try {
            rLock.lock(100L, TimeUnit.MILLISECONDS);
            String redisKey = KEY_PREFIX + path;
            RScoredSortedSet bucket = this.redissonClient.getScoredSortedSet(redisKey);
            RSet pathSet = this.redissonClient.getSet("LeakyBucket::pathSet");
            pathSet.add((Object)path);
            long now = System.currentTimeMillis();
            if ((long)bucket.size() < this.bucketSize) {
                bucket.add((double)now, (Object)now);
                boolean bl = false;
                return bl;
            }
            LogUtils.info((String)("[triggerLimit] path:" + path + " bucket size:" + bucket.size()), (Object[])new Object[0]);
            boolean bl = true;
            return bl;
        }
        finally {
            rLock.unlock();
        }
    }
}

