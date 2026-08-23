package com.kuma.cloud.lab.vector.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 向量检索参数。
 */
@Data
public class VectorSearchDTO {

    /**
     * 文本查询；与 queryVector 二选一，优先使用 queryVector。
     */
    private String query;

    /**
     * 显式查询向量；为空时根据 query 自动生成伪向量。
     */
    private List<Float> queryVector;

    @Min(value = 1, message = "topK 最小为 1")
    @Max(value = 100, message = "topK 最大为 100")
    private int topK = 5;

    /**
     * 最低相似度阈值，低于此分数的命中将被过滤。
     */
    private Double minScore;

    /**
     * 元数据等值过滤条件。
     */
    private Map<String, Object> filter;

}
