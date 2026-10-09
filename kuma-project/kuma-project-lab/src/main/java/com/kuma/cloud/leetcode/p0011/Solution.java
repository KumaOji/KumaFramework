package com.kuma.cloud.leetcode.p0011;

/**
 * 11. 盛最多水的容器。
 * <p>双指针从两端向内移动：面积受较短边限制，移动较长边无法得到更大面积。
 * <p>时间复杂度 O(n)，空间复杂度 O(1)。输入遵循题目约束：至少两条非负高度。
 */
public class Solution {

    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int best = 0;
        while (left < right) {
            best = Math.max(best, Math.min(height[left], height[right]) * (right - left));
            if (height[left] <= height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }
}
