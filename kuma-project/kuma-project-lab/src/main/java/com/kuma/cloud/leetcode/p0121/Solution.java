package com.kuma.cloud.leetcode.p0121;

/**
 * 121. 买卖股票的最佳时机。
 * <p>记录历史最低价，把当天卖出的收益与最佳收益比较；没有盈利机会时返回零。
 * <p>时间复杂度 O(n)，空间复杂度 O(1)。仅允许一次买入和一次之后的卖出。
 */
public class Solution {

    public int maxProfit(int[] prices) {
        int lowest = Integer.MAX_VALUE;
        int best = 0;
        for (int price : prices) {
            lowest = Math.min(lowest, price);
            best = Math.max(best, price - lowest);
        }
        return best;
    }
}
