package com.kuma.cloud.leetcode.doublePoint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class maxOperationsTest {
    private final maxOperations solution = new maxOperations();

    @Test
    void example() {
        int[] nums = {3,1,3,4,3};
        int i = solution.maxOperations2(nums, 6);
        assertEquals(1,i);
    }
}
