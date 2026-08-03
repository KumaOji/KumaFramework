package com.kuma.cloud.leetcode.p0215;

import java.util.PriorityQueue;

/**
 * 215. 数组中的第 K 个最大元素
 *
 * <p>最小堆维护 K 个元素，堆顶即为第 K 大。
 * <p>时间复杂度：O(n log k)，空间复杂度：O(k)。
 */
public class Solution {

    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }
}
