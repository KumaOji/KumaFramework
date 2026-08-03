package com.kuma.cloud.leetcode.p0238;

/**
 * 238. 除自身以外数组的乘积
 *
 * <p>前缀积 + 后缀积：result[i] = 左侧所有元素之积 × 右侧所有元素之积。
 * <p>时间复杂度：O(n)，空间复杂度：O(1)（不含结果数组）。
 */
public class Solution {

    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i];
        }
        return result;
    }
}
