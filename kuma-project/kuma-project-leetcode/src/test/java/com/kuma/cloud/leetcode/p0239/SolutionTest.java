package com.kuma.cloud.leetcode.p0239;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertArrayEquals(
                new int[]{3, 3, 5, 5, 6, 7},
                solution.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)
        );
    }

    @Test
    void example2() {
        assertArrayEquals(new int[]{1}, solution.maxSlidingWindow(new int[]{1}, 1));
    }

    @Test
    void windowEqualsArrayLength() {
        assertArrayEquals(new int[]{7}, solution.maxSlidingWindow(new int[]{3, 1, 7}, 3));
    }
}
