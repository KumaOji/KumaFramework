package com.kuma.boot.data.vector.core;

import com.kuma.boot.data.vector.core.model.Distance;
import com.kuma.boot.data.vector.core.model.VectorDocument;
import com.kuma.boot.data.vector.core.model.VectorMatch;
import com.kuma.boot.data.vector.core.model.VectorSearchRequest;

import java.util.List;
import java.util.Map;

/**
 * 向量库统一抽象。
 *
 * <p>屏蔽底层向量数据库差异（内存 / Qdrant / Milvus / pgvector …），向上层（RAG、语义检索、
 * 推荐召回等）提供一致的「集合管理 + 写入 + 检索 + 删除」能力。所有方法以原始 {@code float[]}
 * 向量为输入，不绑定任何 embedding 框架，调用方自行完成向量化。
 *
 * <p>实现要求线程安全。
 */
public interface VectorStore {

    // ── 集合（collection）管理 ───────────────────────────────────────────────

    /**
     * 创建集合；若已存在则视为幂等（不报错）。
     *
     * @param collection 集合名
     * @param dimension  向量维度
     * @param distance   相似度度量方式
     */
    void createCollection(String collection, int dimension, Distance distance);

    /** 判断集合是否存在 */
    boolean collectionExists(String collection);

    /** 删除集合及其全部数据；不存在则忽略 */
    void deleteCollection(String collection);

    // ── 写入 ────────────────────────────────────────────────────────────────

    /** 批量写入 / 覆盖（按 id upsert）记录 */
    void upsert(String collection, List<VectorDocument> documents);

    /** 写入 / 覆盖单条记录 */
    default void upsert(String collection, VectorDocument document) {
        upsert(collection, List.of(document));
    }

    // ── 检索 ────────────────────────────────────────────────────────────────

    /**
     * 向量相似度检索。
     *
     * @return 按 score 降序排列的命中列表（已应用 topK 与 minScore）
     */
    List<VectorMatch> search(String collection, VectorSearchRequest request);

    // ── 删除 ────────────────────────────────────────────────────────────────

    /** 按 id 批量删除 */
    void delete(String collection, List<String> ids);

    /** 按元数据等值条件删除（key=value 全部满足） */
    void deleteByFilter(String collection, Map<String, Object> filter);

    // ── 统计 ────────────────────────────────────────────────────────────────

    /** 统计集合内记录总数 */
    long count(String collection);

    /** 实现使用的底层向量库标识（如 {@code memory} / {@code qdrant}），便于排查与监控 */
    String provider();
}
