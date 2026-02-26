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

package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于 map 的本地实现
 *
 */
public class CommonCacheServiceMap extends com.kuma.boot.ratelimit.ratelimitbs.extend.cache.AbstractCommonCacheService {

    /**
     * 存储信息
     */
    private Map<String, CommonCacheValueDto> cacheMap;

    /**
     * 清空任务-延迟秒数
     */
    private final long cleanDelaySeconds;

    /**
     * 清空任务-周期秒数
     */
    private final long cleanPeriodSeconds;

    public CommonCacheServiceMap() {
        this(10, 60);
    }

    public CommonCacheServiceMap(long cleanDelaySeconds, long cleanPeriodSeconds) {
        this.cleanDelaySeconds = cleanDelaySeconds;
        this.cleanPeriodSeconds = cleanPeriodSeconds;
        this.initMap();
        this.initCleanTask();
    }

    /**
     * 初始化缓存
     */
    protected void initMap() {
        this.cacheMap = new ConcurrentHashMap<>();
    }

    /**
     * 初始化清理任务
     */
    protected void initCleanTask() {
        final Runnable cleanTask = new com.kuma.boot.ratelimit.ratelimitbs.extend.cache.CommonCacheCleanTask(this.cacheMap);

        // 这里的调度参数，没有必要暴露。
        // 采用和 redis 类似的惰性淘汰即可。
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(cleanTask, cleanDelaySeconds, cleanPeriodSeconds, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void set(String key, String value, long expireMills) {
        long actualMills = 0;
        if (expireMills <= 0) {
            LogUtils.info("过期时间小于等于0，认为不过期");
        } else {
            long currentMills = System.currentTimeMillis();
            actualMills = currentMills + expireMills;
        }

        CommonCacheValueDto dto = CommonCacheValueDto.of(value, actualMills);
        cacheMap.put(key, dto);
    }

    @Override
    public synchronized String set(String key, String value, String nxxx, String expx, int time) {
        this.set(key, value, time);

        // 兼容 jedis
        return "OK";
    }

    @Override
    public String get(String key) {
        checkExpireAndRemove(key);

        CommonCacheValueDto dto = cacheMap.get(key);
        if (dto == null) {
            return null;
        }

        return dto.getValue();
    }

    @Override
    public boolean contains(String key) {
        checkExpireAndRemove(key);

        return cacheMap.containsKey(key);
    }

    @Override
    public synchronized void remove(String key) {
        cacheMap.remove(key);
    }

    /**
     *
     * @param key 获取 key
     * @return 结果
     */
    @Override
    public long ttl(String key) {
        checkExpireAndRemove(key);

        CommonCacheValueDto dto = cacheMap.get(key);
        // 信息不存在
        if (dto == null) {
            return -2L;
        }
        // 没有指定过期时间
        Long expireTime = dto.getExpireTime();
        if (expireTime == null) {
            return -1L;
        }

        // 获取真实的过期时间
        long currentTime = System.currentTimeMillis();
        return expireTime - currentTime;
    }

    @Override
    public void expireAt(String key, long unixTime) {
        // 判断 key 是否存在
        if (contains(key)) {
            CommonCacheValueDto dto = cacheMap.get(key);
            dto.setExpireTime(unixTime);

            cacheMap.put(key, dto);
        }
    }

    @Override
    public long expireAt(String key) {
        checkExpireAndRemove(key);

        CommonCacheValueDto dto = cacheMap.get(key);
        // 信息不存在
        if (dto == null) {
            return -2;
        }

        Long expireTime = dto.getExpireTime();
        if (expireTime == null) {
            return -1;
        }

        return expireTime;
    }

    @Override
    public Object eval(String var1, int var2, String... var3) {
        throw new UnsupportedOperationException();
    }

    /**
     * 当一个信息过期的时候，将其清空。惰性淘汰
     * @param key 键
     */
    private synchronized void checkExpireAndRemove(String key) {
        // 1. 获取
        CommonCacheValueDto dto = cacheMap.get(key);
        if (dto == null) {
            return;
        }

        Long expireTime = dto.getExpireTime();
        if (expireTime != null) {
            long currentMills = System.currentTimeMillis();
            if (expireTime <= currentMills) {
                cacheMap.remove(key);
            }
        }
    }
}
