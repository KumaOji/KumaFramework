package com.kuma.cloud.leetcode.p1768;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void mergesWordsWithSameLength() {
        assertEquals("apbqcr", solution.mergeAlternately("abc", "pqr"));
    }

    @Test
    void appendsRemainingCharactersFromSecondWord() {
        assertEquals("apbqrs", solution.mergeAlternately("ab", "pqrs"));
    }

    @Test
    void appendsRemainingCharactersFromFirstWord() {
        assertEquals("apbqcd", solution.mergeAlternately("abcd", "pq"));
    }
}
