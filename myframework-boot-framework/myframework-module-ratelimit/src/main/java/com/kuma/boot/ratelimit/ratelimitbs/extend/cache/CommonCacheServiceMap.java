/*
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommonCacheServiceMap
extends AbstractCommonCacheService {
    private Map<String, CommonCacheValueDto> cacheMap;
    private final long cleanDelaySeconds;
    private final long cleanPeriodSeconds;

    public CommonCacheServiceMap() {
        this(10L, 60L);
    }

    public CommonCacheServiceMap(long cleanDelaySeconds, long cleanPeriodSeconds) {
        this.cleanDelaySeconds = cleanDelaySeconds;
        this.cleanPeriodSeconds = cleanPeriodSeconds;
        this.initMap();
        this.initCleanTask();
    }

    protected void initMap() {
        this.cacheMap = new ConcurrentHashMap<String, CommonCacheValueDto>();
    }

    protected void initCleanTask() {
        CommonCacheCleanTask cleanTask = new CommonCacheCleanTask(this.cacheMap);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(cleanTask, this.cleanDelaySeconds, this.cleanPeriodSeconds, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void set(String key, String value, long expireMills) {
        long actualMills = 0L;
        if (expireMills <= 0L) {
            LogUtils.info((String)"\u8fc7\u671f\u65f6\u95f4\u5c0f\u4e8e\u7b49\u4e8e0\uff0c\u8ba4\u4e3a\u4e0d\u8fc7\u671f", (Object[])new Object[0]);
        } else {
            long currentMills = System.currentTimeMillis();
            actualMills = currentMills + expireMills;
        }
        CommonCacheValueDto dto = CommonCacheValueDto.of(value, actualMills);
        this.cacheMap.put(key, dto);
    }

    @Override
    public synchronized String set(String key, String value, String nxxx, String expx, int time) {
        this.set(key, value, time);
        return "OK";
    }

    @Override
    public String get(String key) {
        this.checkExpireAndRemove(key);
        CommonCacheValueDto dto = this.cacheMap.get(key);
        if (dto == null) {
            return null;
        }
        return dto.getValue();
    }

    @Override
    public boolean contains(String key) {
        this.checkExpireAndRemove(key);
        return this.cacheMap.containsKey(key);
    }

    @Override
    public synchronized void remove(String key) {
        this.cacheMap.remove(key);
    }

    @Override
    public long ttl(String key) {
        this.checkExpireAndRemove(key);
        CommonCacheValueDto dto = this.cacheMap.get(key);
        if (dto == null) {
            return -2L;
        }
        Long expireTime = dto.getExpireTime();
        if (expireTime == null) {
            return -1L;
        }
        long currentTime = System.currentTimeMillis();
        return expireTime - currentTime;
    }

    @Override
    public void expireAt(String key, long unixTime) {
        if (this.contains(key)) {
            CommonCacheValueDto dto = this.cacheMap.get(key);
            dto.setExpireTime(unixTime);
            this.cacheMap.put(key, dto);
        }
    }

    @Override
    public long expireAt(String key) {
        this.checkExpireAndRemove(key);
        CommonCacheValueDto dto = this.cacheMap.get(key);
        if (dto == null) {
            return -2L;
        }
        Long expireTime = dto.getExpireTime();
        if (expireTime == null) {
            return -1L;
        }
        return expireTime;
    }

    @Override
    public Object eval(String var1, int var2, String ... var3) {
        throw new UnsupportedOperationException();
    }

    private synchronized void checkExpireAndRemove(String key) {
        CommonCacheValueDto dto = this.cacheMap.get(key);
        if (dto == null) {
            return;
        }
        Long expireTime = dto.getExpireTime();
        if (expireTime != null) {
            long currentMills = System.currentTimeMillis();
            if (expireTime <= currentMills) {
                this.cacheMap.remove(key);
            }
        }
    }
}

