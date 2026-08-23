package com.kuma.cloud.leetcode.p0283;

/**
 * 283. 移动零
 *
 * <p>时间复杂度：O(n)，空间复杂度：O(1)。
 */
public class Solution {

    public void moveZeroes(int[] nums) {
        int write = 0;
        for (int num : nums) {
            if (num != 0) {
                nums[write++] = num;
            }
        }
        while (write < nums.length) {
            nums[write++] = 0;
        }
    }
}
