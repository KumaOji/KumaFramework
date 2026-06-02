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
        // 原子写入：仅当不存在或已过期时才占用成功
        Long previous = cache.merge(key, expireAt, (oldExpire, newExpire) ->
                oldExpire <= now ? newExpire : oldExpire);
        // merge 返回最终值；若最终值等于本次写入的 expireAt，说明占用成功
        return previous == expireAt;
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
