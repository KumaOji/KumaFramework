package com.kuma.cloud.leetcode.p0443;

/**
 * 443. 压缩字符串
 *
 * <p>时间复杂度：O(n)，空间复杂度：O(1)。
 */
public class Solution {

    public int compress(char[] chars) {
        int read = 0;
        int write = 0;

        while (read < chars.length) {
            char current = chars[read];
            int groupStart = read;
            while (read < chars.length && chars[read] == current) {
                read++;
            }

            chars[write++] = current;
            int count = read - groupStart;
            if (count > 1) {
                for (char digit : String.valueOf(count).toCharArray()) {
                    chars[write++] = digit;
                }
            }
        }
        return write;
    }
}
