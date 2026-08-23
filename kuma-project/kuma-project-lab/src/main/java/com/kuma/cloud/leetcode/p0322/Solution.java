package com.kuma.cloud.leetcode.p0322;

import java.util.Arrays;

/**
 * 322. 零钱兑换
 *
 * <p>完全背包 DP：dp[i] 表示凑出金额 i 所需最少硬币数。
 * <p>时间复杂度：O(amount × n)，空间复杂度：O(amount)。
 */
public class Solution {

    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }
}
