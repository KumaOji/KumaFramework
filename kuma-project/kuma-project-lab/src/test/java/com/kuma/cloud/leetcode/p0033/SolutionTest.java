package com.kuma.cloud.leetcode.p0033;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {
    private final Solution solution = new Solution();

    @Test
    void examplesAndBoundaries() {
        assertEquals(4, solution.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 0));
        assertEquals(-1, solution.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 3));
        assertEquals(0, solution.search(new int[]{1}, 1));
        assertEquals(-1, solution.search(new int[]{1}, 0));
        assertEquals(1, solution.search(new int[]{3, 1}, 1));
    }

    @Test
    void findsEveryElementAcrossAllRotationPoints() {
        int[] sorted = {-10, -3, 0, 4, 8, 12, 19};
        for (int rotation = 0; rotation < sorted.length; rotation++) {
            int[] nums = new int[sorted.length];
            for (int i = 0; i < nums.length; i++) {
                nums[i] = sorted[(i + rotation) % sorted.length];
            }
            for (int i = 0; i < nums.length; i++) {
                assertEquals(i, solution.search(nums, nums[i]));
            }
            assertEquals(-1, solution.search(nums, 5));
            assertEquals(-1, solution.search(nums, -11));
            assertEquals(-1, solution.search(nums, 20));
        }
    }
}
