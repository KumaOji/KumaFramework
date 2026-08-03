package com.kuma.cloud.leetcode.p0003;

import java.util.Arrays;

/**
 * 3. 无重复字符的最长子串
 *
 * <p>滑动窗口：右指针扩展窗口，左指针跳过重复字符。
 * <p>时间复杂度：O(n)，空间复杂度：O(1)。
 */
public class Solution {

    public int lengthOfLongestSubstring(String s) {
        int[] lastSeen = new int[128];
        Arrays.fill(lastSeen, -1);
        int max = 0;
        int left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastSeen[c] >= left) {
                left = lastSeen[c] + 1;
            }
            lastSeen[c] = right;
            max = Math.max(max, right - left + 1);
        }
        return max;
    }
}
