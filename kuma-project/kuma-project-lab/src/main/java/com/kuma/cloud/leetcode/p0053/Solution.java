package com.kuma.cloud.leetcode.p0053;

/**
 * 53. 最大子数组和
 *
 * <p>Kadane 算法：维护以当前位置结尾的最大子数组和。
 * <p>时间复杂度：O(n)，空间复杂度：O(1)。
 */
public class Solution {

    public int maxSubArray(int[] nums) {
        int current = nums[0];
        int best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            best = Math.max(best, current);
        }
        return best;
    }
}
