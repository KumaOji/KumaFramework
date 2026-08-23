package com.kuma.cloud.lab.vector.domain.vo;

import java.util.List;
import java.util.Map;

/**
 * 向量记录视图。
 */
public record VectorDocumentVO(
        String id,
        String content,
        List<Float> vector,
        Map<String, Object> metadata
) {
}
