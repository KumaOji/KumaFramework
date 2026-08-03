package com.kuma.cloud.lab.redis.domain.vo;

/**
 * Redis 读取结果。
 */
public record RedisValueVO(
        String key,
        String type,
        Object value,
        Long ttlSeconds
) {
}
