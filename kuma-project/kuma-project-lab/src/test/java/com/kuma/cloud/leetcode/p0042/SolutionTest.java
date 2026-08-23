package com.kuma.cloud.leetcode.p0042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertEquals(6, solution.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
    }

    @Test
    void example2() {
        assertEquals(9, solution.trap(new int[]{4, 2, 0, 3, 2, 5}));
    }

    @Test
    void flatSurface() {
        assertEquals(0, solution.trap(new int[]{1, 1, 1}));
    }
}
