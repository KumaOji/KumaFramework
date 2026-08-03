package com.kuma.cloud.leetcode.p0056;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertArrayEquals(
                new int[][]{{1, 6}, {8, 10}, {15, 18}},
                solution.merge(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}})
        );
    }

    @Test
    void example2() {
        assertArrayEquals(
                new int[][]{{1, 5}},
                solution.merge(new int[][]{{1, 4}, {4, 5}})
        );
    }
}
