package com.kuma.cloud.leetcode.p0704;

/**
 * 704. 二分查找。
 * <p>维护闭区间 [left, right]，每轮排除中点及不可能包含目标的一半。
 * <p>时间复杂度 O(log n)，空间复杂度 O(1)。输入数组升序且元素互不相同。
 */
public class Solution {

    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
}
