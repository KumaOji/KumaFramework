package com.kuma.cloud.lab.vector.domain.vo;

import java.util.List;

/**
 * 向量场景测试结果。
 */
public record VectorScenarioVO(
        String collection,
        String provider,
        int dimension,
        List<VectorOperationStepVO> steps
) {
}
