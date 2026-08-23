package com.kuma.cloud.leetcode.p0053;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertEquals(6, solution.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
    }

    @Test
    void singleElement() {
        assertEquals(1, solution.maxSubArray(new int[]{1}));
    }

    @Test
    void allNegative() {
        assertEquals(-1, solution.maxSubArray(new int[]{-2, -1}));
    }
}
