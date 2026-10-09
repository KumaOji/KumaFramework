package com.kuma.cloud.leetcode.p0198;

/**
 * 198. 打家劫舍。
 * <p>当前最优收益 = max(不选当前房屋的收益，前两间最优收益 + 当前金额)。
 * <p>滚动保存前两个状态，时间复杂度 O(n)，空间复杂度 O(1)。金额为非负整数。
 */
public class Solution {

    public int rob(int[] nums) {
        int prev2 = 0;
        int prev1 = 0;
        for (int amount : nums) {
            int current = Math.max(prev1, prev2 + amount);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
