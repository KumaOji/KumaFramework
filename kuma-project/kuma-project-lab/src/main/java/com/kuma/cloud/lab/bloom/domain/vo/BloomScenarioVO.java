package com.kuma.cloud.lab.bloom.domain.vo;

import java.util.List;

/**
 * 布隆过滤器场景测试结果。
 */
public record BloomScenarioVO(
        int bitSize,
        int hashFunctions,
        int insertedCount,
        int setBitCount,
        List<BloomOperationStepVO> steps
) {
}
