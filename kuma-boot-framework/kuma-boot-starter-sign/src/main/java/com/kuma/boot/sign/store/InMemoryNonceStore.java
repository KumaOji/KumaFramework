package com.kuma.boot.sign.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;

/**
 * 基于内存的 nonce 去重实现（单机有效）
 *
 * <p>使用 {@link ConcurrentHashMap} 记录 nonce 过期时间，后台定时清理过期项；
 * 分布式场景请改用 Redis 实现。
 */
public class InMemoryNonceStore implements NonceStore, DisposableBean {

    /** key → expireAt(epochMillis) */
    private final Map<String, Long> cache = new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleaner;

    public InMemoryNonceStore() {
        this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sign-nonce-cleaner");
            t.setDaemon(true);
            return t;
        });
        this.cleaner.scheduleWithFixedDelay(this::evictExpired, 60L, 60L, TimeUnit.SECONDS);
    }

    @Override
    public boolean tryAcquire(String key, long ttlSeconds) {
        long now = System.currentTimeMillis();
        long expireAt = now + ttlSeconds * 1000L;
        // 第一次写入：key 不存在时原子插入
        Long existing = cache.putIfAbsent(key, expireAt);
        if (existing == null) {
            return true;
        }
        // key 已存在但已过期：原子 CAS 替换（只有一个并发线程能成功）
        if (existing <= now) {
            return cache.replace(key, existing, expireAt);
        }
        return false;
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(e -> e.getValue() <= now);
    }

    @Override
    public void destroy() {
        cleaner.shutdownNow();
        cache.clear();
    }
}
