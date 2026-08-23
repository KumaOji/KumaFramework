package com.kuma.cloud.leetcode.p0239;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 239. 滑动窗口最大值
 *
 * <p>单调递减队列：队头始终是当前窗口最大值的下标。
 * <p>时间复杂度：O(n)，空间复杂度：O(k)。
 */
public class Solution {

    public int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> deque = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }
}
