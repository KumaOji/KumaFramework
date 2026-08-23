package com.kuma.cloud.lab.vector.domain.vo;

/**
 * 向量库状态信息。
 */
public record VectorStoreStatusVO(
        String provider,
        String collection,
        boolean collectionExists,
        long documentCount,
        int dimension
) {
}
