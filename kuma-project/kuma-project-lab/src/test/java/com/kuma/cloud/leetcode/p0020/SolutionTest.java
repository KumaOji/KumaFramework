package com.kuma.cloud.leetcode.p0020;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void example1() {
        assertTrue(solution.isValid("()"));
    }

    @Test
    void example2() {
        assertTrue(solution.isValid("()[]{}"));
    }

    @Test
    void rejectsMismatchedBrackets() {
        assertFalse(solution.isValid("(]"));
    }

    @Test
    void rejectsUnclosedBrackets() {
        assertFalse(solution.isValid("(("));
    }
}
