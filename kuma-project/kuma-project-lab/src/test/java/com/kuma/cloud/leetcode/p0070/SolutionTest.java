package com.kuma.cloud.leetcode.p0070;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertEquals(3, solution.climbStairs(3));
    }

    @Test
    void example2() {
        assertEquals(8, solution.climbStairs(5));
    }

    @Test
    void baseCases() {
        assertEquals(1, solution.climbStairs(1));
        assertEquals(2, solution.climbStairs(2));
    }
}
