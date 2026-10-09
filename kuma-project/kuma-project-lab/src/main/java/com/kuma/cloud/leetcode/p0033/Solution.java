package com.kuma.cloud.leetcode.p0033;

/**
 * 33. 搜索旋转排序数组。
 * <p>每次二分至少有一半有序，根据 target 是否落在该有序区间决定保留哪一半。
 * <p>时间复杂度 O(log n)，空间复杂度 O(1)。输入为升序数组的旋转，元素互不相同。
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
            if (nums[left] <= nums[mid]) {
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }
}
