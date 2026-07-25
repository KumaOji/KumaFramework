package com.kuma.cloud.leetcode.p0283;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void movesZeroesToTheEnd() {
        int[] nums = {0, 1, 0, 3, 12};

        solution.moveZeroes(nums);

        assertArrayEquals(new int[]{1, 3, 12, 0, 0}, nums);
    }

    @Test
    void keepsArrayWithoutZeroUnchanged() {
        int[] nums = {1, 2, 3};

        solution.moveZeroes(nums);

        assertArrayEquals(new int[]{1, 2, 3}, nums);
    }
}
