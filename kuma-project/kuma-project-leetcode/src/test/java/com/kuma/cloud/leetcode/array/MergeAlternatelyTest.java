package com.kuma.cloud.leetcode.array;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MergeAlternatelyTest {

    private final MergeAlternately solution = new MergeAlternately();

    @Test
    void example1() {
        solution.mergeAlternately("abc","pqr");
        assertEquals("apbqcr", solution.mergeAlternately("abc","pqr"));
    }

    @Test
    void example2() {
        assertEquals("apbqrs", solution.mergeAlternately("ab","pqrs"));
    }

    @Test
    void example3() {
        assertEquals("apbqcd", solution.mergeAlternately("abcd","pq"));
    }
}
