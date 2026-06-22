package com.kuma.boot.data.vector.core.model;

import java.util.Map;

/**
 * 待写入向量库的一条记录。
 *
 * @param id       记录唯一标识；为空时由实现自动生成（UUID）
 * @param vector   稠密向量（embedding），长度需与目标 collection 的维度一致
 * @param content  原始文本内容，随向量一并存入 payload，便于检索后直接返回
 * @param metadata 业务元数据（如 source / 分类 / 时间戳），支持作为检索过滤条件
 */
public record VectorDocument(String id, float[] vector, String content, Map<String, Object> metadata) {

    public VectorDocument {
        metadata = (metadata == null) ? Map.of() : metadata;
    }

    /** 无元数据的便捷构造 */
    public static VectorDocument of(String id, float[] vector, String content) {
        return new VectorDocument(id, vector, content, Map.of());
    }
}
