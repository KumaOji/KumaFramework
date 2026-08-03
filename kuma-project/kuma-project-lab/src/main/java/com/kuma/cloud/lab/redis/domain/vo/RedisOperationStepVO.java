package com.kuma.cloud.lab.redis.domain.vo;

/**
 * Redis 场景测试中的单步操作记录。
 */
public record RedisOperationStepVO(
        String operation,
        String key,
        Object before,
        Object after,
        Long ttlSeconds
) {
}
