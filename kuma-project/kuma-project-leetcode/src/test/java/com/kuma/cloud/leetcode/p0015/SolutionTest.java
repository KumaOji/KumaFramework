package com.kuma.cloud.leetcode.p0015;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertEquals(
                List.of(List.of(-1, -1, 2), List.of(-1, 0, 1)),
                solution.threeSum(new int[]{-1, 0, 1, 2, -1, -4})
        );
    }

    @Test
    void example2() {
        assertEquals(List.of(), solution.threeSum(new int[]{0, 1, 1}));
    }

    @Test
    void example3() {
        assertEquals(List.of(List.of(0, 0, 0)), solution.threeSum(new int[]{0, 0, 0}));
    }
}
