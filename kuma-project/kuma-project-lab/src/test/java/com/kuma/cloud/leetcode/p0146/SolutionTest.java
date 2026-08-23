package com.kuma.cloud.leetcode.p0146;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    @Test
    void example1() {
        Solution.LRUCache cache = new Solution.LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        assertEquals(1, cache.get(1));
        cache.put(3, 3);
        assertEquals(-1, cache.get(2));
        cache.put(4, 4);
        assertEquals(-1, cache.get(1));
        assertEquals(3, cache.get(3));
        assertEquals(4, cache.get(4));
    }

    @Test
    void updateExistingKey() {
        Solution.LRUCache cache = new Solution.LRUCache(2);
        cache.put(1, 1);
        cache.put(1, 10);
        assertEquals(10, cache.get(1));
    }
}
