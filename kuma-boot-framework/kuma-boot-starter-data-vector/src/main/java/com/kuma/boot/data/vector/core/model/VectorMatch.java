package com.kuma.boot.data.vector.core.model;

import java.util.Map;

/**
 * 一条向量检索命中结果。
 *
 * @param id       命中记录的标识
 * @param score    相似度分数，统一语义为「越大越相似」（已按度量方式归一化）
 * @param content  命中记录的原始文本内容
 * @param metadata 命中记录的业务元数据
 */
public record VectorMatch(String id, double score, String content, Map<String, Object> metadata) {

    public VectorMatch {
        metadata = (metadata == null) ? Map.of() : metadata;
    }
}
