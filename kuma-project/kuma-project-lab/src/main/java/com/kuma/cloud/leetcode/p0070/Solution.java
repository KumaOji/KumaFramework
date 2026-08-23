package com.kuma.cloud.leetcode.p0070;

/**
 * 70. 爬楼梯
 *
 * <p>动态规划：到达第 n 阶的方法数 = 到达 n-1 阶 + 到达 n-2 阶。
 * <p>时间复杂度：O(n)，空间复杂度：O(1)。
 */
public class Solution {

    public int climbStairs(int n) {
        if (n <= 2) {
            return n;
        }
        int prev2 = 1;
        int prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
