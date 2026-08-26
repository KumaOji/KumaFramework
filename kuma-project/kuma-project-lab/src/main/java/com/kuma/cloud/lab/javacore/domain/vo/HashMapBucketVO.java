package com.kuma.cloud.lab.javacore.domain.vo;

/**
 * HashMap 单个 key 的桶位信息。
 */
public record HashMapBucketVO(
        String key,
        int keyHashCode,
        int spreadHash,
        int bucketIndex,
        String formula
) {
}
