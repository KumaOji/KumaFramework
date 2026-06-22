package com.kuma.boot.data.vector.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 向量检索请求。
 *
 * <pre>{@code
 * VectorSearchRequest req = VectorSearchRequest.builder()
 *         .queryVector(embedding)
 *         .topK(5)
 *         .minScore(0.6)
 *         .filter(Map.of("source", "handbook.md"))
 *         .build();
 * }</pre>
 */
@Data
@Builder
public class VectorSearchRequest {

    /** 查询向量（已 embedding），长度需与 collection 维度一致 */
    private float[] queryVector;

    /** 返回的最大命中数 */
    @Builder.Default
    private int topK = 5;

    /** 最低相似度阈值，低于此分数的命中将被过滤；0 表示不过滤 */
    @Builder.Default
    private double minScore = 0.0;

    /** 元数据等值过滤条件（key=value 全部满足），为空则不过滤 */
    private Map<String, Object> filter;
}
