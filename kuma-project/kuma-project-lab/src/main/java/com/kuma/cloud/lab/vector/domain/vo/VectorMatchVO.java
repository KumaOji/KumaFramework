package com.kuma.cloud.lab.vector.domain.vo;

import java.util.Map;

/**
 * 向量检索命中结果。
 */
public record VectorMatchVO(
        String id,
        double score,
        String content,
        Map<String, Object> metadata
) {
}
