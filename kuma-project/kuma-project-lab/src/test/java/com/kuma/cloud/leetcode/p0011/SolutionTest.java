package com.kuma.cloud.leetcode.p0011;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {
    private final Solution solution = new Solution();

    @Test
    void examplesAndBoundaries() {
        assertEquals(49, solution.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
        assertEquals(1, solution.maxArea(new int[]{1, 1}));
        assertEquals(12, solution.maxArea(new int[]{4, 4, 4, 4}));
        assertEquals(0, solution.maxArea(new int[]{0, 0}));
        assertEquals(6, solution.maxArea(new int[]{1, 2, 3, 4, 5}));
        assertEquals(6, solution.maxArea(new int[]{5, 4, 3, 2, 1}));
    }
}
