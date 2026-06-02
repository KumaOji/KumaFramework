package com.kuma.boot.sign.store;

/**
 * nonce 去重存储（防重放）
 *
 * <p>默认实现 {@code InMemoryNonceStore} 适用于单机；分布式部署应注册基于 Redis 的实现覆盖。
 */
public interface NonceStore {

    /**
     * 尝试占用一个 nonce
     *
     * @param key        去重键（通常为 appId:nonce）
     * @param ttlSeconds 保留时长（秒）
     * @return {@code true} 表示首次出现占用成功；{@code false} 表示已存在（重复请求）
     */
    boolean tryAcquire(String key, long ttlSeconds);
}
