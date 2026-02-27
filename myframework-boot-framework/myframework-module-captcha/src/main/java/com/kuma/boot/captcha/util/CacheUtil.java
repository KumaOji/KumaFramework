/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.captcha.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheUtil {
    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);
    private static final Map<String, Object> CACHE_MAP = new ConcurrentHashMap<String, Object>();
    private static Integer CACHE_MAX_NUMBER = 1000;
    private static ScheduledExecutorService scheduledExecutor;

    public static void init(int cacheMaxNumber, long second) {
        CACHE_MAX_NUMBER = cacheMaxNumber;
        if (second > 0L) {
            scheduledExecutor = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "ttc-captcha-cache-clean-executor"), new ThreadPoolExecutor.CallerRunsPolicy());
            scheduledExecutor.scheduleAtFixedRate(CacheUtil::refresh, 10L, second, TimeUnit.SECONDS);
        }
    }

    public static void refresh() {
        logger.debug("local\u7f13\u5b58\u5237\u65b0,\u6e05\u9664\u8fc7\u671f\u6570\u636e");
        for (String key : CACHE_MAP.keySet()) {
            CacheUtil.exists(key);
        }
    }

    public static void set(String key, String value, long expiresInSeconds) {
        if (CACHE_MAP.size() > CACHE_MAX_NUMBER * 2) {
            logger.info("CACHE_MAP\u8fbe\u5230\u9608\u503c\uff0cclear map");
            CacheUtil.clear();
        }
        CACHE_MAP.put(key, value);
        if (expiresInSeconds > 0L) {
            CACHE_MAP.put(key + "_HoldTime", System.currentTimeMillis() + expiresInSeconds * 1000L);
        }
    }

    public static void delete(String key) {
        CACHE_MAP.remove(key);
        CACHE_MAP.remove(key + "_HoldTime");
    }

    public static boolean exists(String key) {
        Long cacheHoldTime = (Long)CACHE_MAP.get(key + "_HoldTime");
        if (cacheHoldTime == null || cacheHoldTime == 0L) {
            return false;
        }
        if (cacheHoldTime < System.currentTimeMillis()) {
            CacheUtil.delete(key);
            return false;
        }
        return true;
    }

    public static String get(String key) {
        if (CacheUtil.exists(key)) {
            return (String)CACHE_MAP.get(key);
        }
        return null;
    }

    public static void clear() {
        logger.debug("have clean all key !");
        CACHE_MAP.clear();
    }
}

