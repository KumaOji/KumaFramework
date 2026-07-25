package com.kuma.cloud.leetcode.p1679;

import java.util.HashMap;
import java.util.Map;

/**
 * 1679. K 和数对的最大数目
 *
 * <p>时间复杂度：O(n)，空间复杂度：O(n)。
 */
public class Solution {

    public int maxOperations(int[] nums, int k) {
        Map<Integer, Integer> available = new HashMap<>();
        int operations = 0;

        for (int num : nums) {
            int complement = k - num;
            int count = available.getOrDefault(complement, 0);
            if (count > 0) {
                operations++;
                if (count == 1) {
                    available.remove(complement);
                } else {
                    available.put(complement, count - 1);
                }
            } else {
                available.merge(num, 1, Integer::sum);
            }
        }
        return operations;
    }
}
