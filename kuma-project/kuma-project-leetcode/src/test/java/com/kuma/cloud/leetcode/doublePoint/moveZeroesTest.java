package com.kuma.cloud.leetcode.doublePoint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class moveZeroesTest {
    private final moveZeroes solution = new moveZeroes();

    @Test
    void example1() {
        int[] nums = {0,1,0,3,12};
        solution.moveZeroes(nums);
        assertArrayEquals(new int[]{1,3,12,0,0}, nums);
    }
}
