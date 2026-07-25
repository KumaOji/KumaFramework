package com.kuma.cloud.leetcode.p0001;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertArrayEquals(new int[]{0, 1}, solution.twoSum(new int[]{2, 7, 11, 15}, 9));
    }

    @Test
    void example2() {
        assertArrayEquals(new int[]{1, 2}, solution.twoSum(new int[]{3, 2, 4}, 6));
    }

    @Test
    void supportsDuplicateNumbers() {
        assertArrayEquals(new int[]{0, 1}, solution.twoSum(new int[]{3, 3}, 6));
    }

    @Test
    void rejectsInputWithoutSolution() {
        assertThrows(IllegalArgumentException.class, () -> solution.twoSum(new int[]{1, 2}, 10));
    }
}
