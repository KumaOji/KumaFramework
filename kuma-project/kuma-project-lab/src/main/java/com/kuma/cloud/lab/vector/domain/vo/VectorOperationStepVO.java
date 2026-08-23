package com.kuma.cloud.lab.vector.domain.vo;

/**
 * 向量场景测试中的单步操作记录。
 */
public record VectorOperationStepVO(
        String operation,
        Object input,
        Object output
) {
}
