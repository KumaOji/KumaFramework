package com.kuma.cloud.leetcode.p1768;

/**
 * 1768. 交替合并字符串
 *
 * <p>时间复杂度：O(m + n)，空间复杂度：O(m + n)。
 */
public class Solution {

    public String mergeAlternately(String word1, String word2) {
        StringBuilder result = new StringBuilder(word1.length() + word2.length());
        int maxLength = Math.max(word1.length(), word2.length());
        for (int i = 0; i < maxLength; i++) {
            if (i < word1.length()) {
                result.append(word1.charAt(i));
            }
            if (i < word2.length()) {
                result.append(word2.charAt(i));
            }
        }
        return result.toString();
    }
}
