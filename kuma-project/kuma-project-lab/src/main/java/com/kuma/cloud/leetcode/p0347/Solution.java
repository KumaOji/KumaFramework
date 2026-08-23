package com.kuma.cloud.leetcode.p0347;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 347. 前 K 个高频元素
 *
 * <p>哈希表统计频次 + 最小堆维护前 K 个高频元素。
 * <p>时间复杂度：O(n log k)，空间复杂度：O(n)。
 */
public class Solution {

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (int num : nums) {
            frequency.merge(num, 1, Integer::sum);
        }
        PriorityQueue<Integer> minHeap = new PriorityQueue<>((a, b) -> frequency.get(a) - frequency.get(b));
        for (int num : frequency.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll();
        }
        return result;
    }
}
