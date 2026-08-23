package com.kuma.cloud.leetcode.p0001;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 两数之和
 *
 * <p>时间复杂度：O(n)，空间复杂度：O(n)。
 */
public class Solution {

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            Integer complementIndex = seen.get(complement);
            if (complementIndex != null) {
                return new int[]{complementIndex, i};
            }
            seen.put(nums[i], i);
        }
        throw new IllegalArgumentException("不存在满足条件的两个数");
    }
}
