package com.kuma.cloud.leetcode.array;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 两数之和
 * <p>
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。
 * <p>
 * 你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。你可以按任意顺序返回答案。
 *
 * @see <a href="https://leetcode.cn/problems/two-sum/">LeetCode 1. 两数之和</a>
 */
public class TwoSum {

    /**
     * 哈希表一次遍历：对每个 nums[i]，查找 target - nums[i] 是否已出现。
     * 时间 O(n)，空间 O(n)。
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> indexByValue = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            Integer j = indexByValue.get(need);
            if (j != null) {
                return new int[]{j, i};
            }
            indexByValue.put(nums[i], i);
        }
        throw new IllegalArgumentException("no solution");
    }
}
