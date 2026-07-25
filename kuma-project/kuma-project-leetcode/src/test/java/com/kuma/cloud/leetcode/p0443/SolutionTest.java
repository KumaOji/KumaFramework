package com.kuma.cloud.leetcode.p0443;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void compressesRepeatedCharacters() {
        char[] chars = {'a', 'a', 'b', 'b', 'c', 'c', 'c'};

        int length = solution.compress(chars);

        assertEquals(6, length);
        assertArrayEquals(new char[]{'a', '2', 'b', '2', 'c', '3'}, Arrays.copyOf(chars, length));
    }

    @Test
    void writesEveryDigitOfAGroupLength() {
        char[] chars = {'a', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'};

        int length = solution.compress(chars);

        assertEquals(4, length);
        assertArrayEquals(new char[]{'a', 'b', '1', '2'}, Arrays.copyOf(chars, length));
    }
}
