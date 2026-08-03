package com.kuma.cloud.leetcode.p0042;

/**
 * 42. 接雨水
 *
 * <p>双指针：左右各维护最大高度，较低一侧决定当前位置能接的水量。
 * <p>时间复杂度：O(n)，空间复杂度：O(1)。
 */
public class Solution {

    public int trap(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;
        while (left < right) {
            if (height[left] <= height[right]) {
                leftMax = Math.max(leftMax, height[left]);
                water += leftMax - height[left];
                left++;
            } else {
                rightMax = Math.max(rightMax, height[right]);
                water += rightMax - height[right];
                right--;
            }
        }
        return water;
    }
}
