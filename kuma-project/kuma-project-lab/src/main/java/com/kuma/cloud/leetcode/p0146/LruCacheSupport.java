package com.kuma.cloud.leetcode.p0146;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 供 Lab 等外部模块调用的 LRU 缓存执行工具。
 */
public final class LruCacheSupport {

    private LruCacheSupport() {
    }

    public static List<Integer> execute(int capacity, List<Map<String, Object>> operations) {
        Solution.LRUCache cache = new Solution.LRUCache(capacity);
        List<Integer> results = new ArrayList<>();
        for (Map<String, Object> operation : operations) {
            String op = String.valueOf(operation.get("op"));
            if ("get".equals(op)) {
                results.add(cache.get(((Number) operation.get("key")).intValue()));
            } else if ("put".equals(op)) {
                cache.put(((Number) operation.get("key")).intValue(),
                        ((Number) operation.get("value")).intValue());
            } else {
                throw new IllegalArgumentException("不支持的 LRU 操作: " + op);
            }
        }
        return results;
    }

}
