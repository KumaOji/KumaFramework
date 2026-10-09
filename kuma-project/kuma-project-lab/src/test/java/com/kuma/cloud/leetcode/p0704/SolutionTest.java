package com.kuma.cloud.leetcode.p0704;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {
    private final Solution solution = new Solution();

    @Test
    void examplesAndSearchBoundaries() {
        int[] nums = {-1, 0, 3, 5, 9, 12};
        for (int i = 0; i < nums.length; i++) {
            assertEquals(i, solution.search(nums, nums[i]));
        }
        assertEquals(-1, solution.search(nums, 2));
        assertEquals(-1, solution.search(nums, -2));
        assertEquals(-1, solution.search(nums, 13));
        assertEquals(0, solution.search(new int[]{5}, 5));
        assertEquals(-1, solution.search(new int[]{5}, 4));
        assertEquals(1, solution.search(new int[]{1, 3}, 3));
    }
}
