package com.kuma.cloud.lab.snowflake.domain.vo;

import java.util.List;

/**
 * 雪花算法场景测试结果。
 */
public record SnowflakeScenarioVO(
        long workerId,
        long datacenterId,
        int generatedCount,
        boolean strictlyIncreasing,
        List<SnowflakeOperationStepVO> steps
) {
}
