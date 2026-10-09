package com.kuma.cloud.leetcode.p0121;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {
    private final Solution solution = new Solution();

    @Test
    void examplesAndTransactionOrder() {
        assertEquals(5, solution.maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
        assertEquals(0, solution.maxProfit(new int[]{7, 6, 4, 3, 1}));
        assertEquals(0, solution.maxProfit(new int[]{5}));
        assertEquals(0, solution.maxProfit(new int[]{3, 3, 3}));
        assertEquals(2, solution.maxProfit(new int[]{2, 4, 1}));
        assertEquals(4, solution.maxProfit(new int[]{1, 2, 3, 4, 5}));
    }
}
