package com.kuma.cloud.leetcode.p0198;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {
    private final Solution solution = new Solution();

    @Test
    void examplesAndNonAdjacentChoices() {
        assertEquals(4, solution.rob(new int[]{1, 2, 3, 1}));
        assertEquals(12, solution.rob(new int[]{2, 7, 9, 3, 1}));
        assertEquals(5, solution.rob(new int[]{5}));
        assertEquals(7, solution.rob(new int[]{2, 7}));
        assertEquals(4, solution.rob(new int[]{2, 1, 1, 2}));
        assertEquals(0, solution.rob(new int[]{0, 0, 0}));
    }
}
