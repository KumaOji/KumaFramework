package com.kuma.cloud.lab.snowflake.domain.vo;

/**
 * 雪花算法场景测试中的单步记录。
 */
public record SnowflakeOperationStepVO(
        String operation,
        long id,
        String timestampText,
        long datacenterId,
        long workerId,
        long sequence,
        String note
) {
}
