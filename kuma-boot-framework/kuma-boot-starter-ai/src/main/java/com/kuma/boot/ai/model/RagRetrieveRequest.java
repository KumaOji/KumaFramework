package com.kuma.boot.ai.model;

import lombok.Data;

/**
 * 检索测试请求体。topK / minScore 留空则使用 {@code kuma.boot.ai.rag.*} 配置默认值。
 */
@Data
public class RagRetrieveRequest {
    /** 查询文本 */
    private String query;
    /** 返回的最大段落数（可选） */
    private Integer topK;
    /** 最低相似度阈值 0~1（可选） */
    private Double minScore;
}
