package com.kuma.cloud.lab.bloom.domain.vo;

/**
 * 布隆过滤器场景测试中的单步操作记录。
 */
public record BloomOperationStepVO(
        String operation,
        String value,
        boolean result,
        String note
) {
}
