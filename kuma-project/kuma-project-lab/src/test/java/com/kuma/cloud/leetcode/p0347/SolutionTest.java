package com.kuma.cloud.leetcode.p0347;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        int[] result = solution.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2);
        Arrays.sort(result);
        assertArrayEquals(new int[]{1, 2}, result);
    }

    @Test
    void singleElement() {
        assertArrayEquals(new int[]{1}, solution.topKFrequent(new int[]{1}, 1));
    }

    @Test
    void allSameFrequency() {
        int[] result = solution.topKFrequent(new int[]{4, 1, 2, 3}, 2);
        assertEquals(2, result.length);
        assertTrue(Set.of(1, 2, 3, 4).containsAll(Arrays.stream(result).boxed().toList()));
    }
}
