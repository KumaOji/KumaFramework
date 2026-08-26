package com.kuma.cloud.lab.javacore.domain.vo;

import java.util.List;

/**
 * HashMap 内部结构观察结果。
 */
public record HashMapInspectVO(
        int entryCount,
        int tableLength,
        int threshold,
        float loadFactor,
        List<HashMapBucketVO> placements,
        List<String> structureNotes
) {
}
