package com.kuma.cloud.lab.redis.domain.vo;

import java.util.List;

/**
 * Redis 场景测试结果。
 */
public record RedisScenarioVO(
        String keyPrefix,
        List<RedisOperationStepVO> steps
) {
}
