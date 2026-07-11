package com.kuma.cloud.leetcode.array;

import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 给你一个字符数组 chars ，请使用下述算法压缩：

 从一个空字符串 s 开始。对于 chars 中的每组 连续重复字符 ：

 如果这一组长度为 1 ，则将字符追加到 s 中。
 否则，需要向 s 追加字符，后跟这一组的长度。
 压缩后得到的字符串 s 不应该直接返回 ，需要转储到字符数组 chars 中。需要注意的是，如果组长度为 10 或 10 以上，则在 chars 数组中会被拆分为多个字符。

 请在 修改完输入数组后 ，返回该数组的新长度。

 你必须设计并实现一个只使用常量额外空间的算法来解决此问题。

 注意：数组中超出返回长度的字符无关紧要，应予忽略。



 示例 1：

 输入：chars = ["a","a","b","b","c","c","c"]
 输出：6
 解释：分组是 "aa"、"bb" 和 "ccc"，压缩为 "a2b2c3"。
 在原地修改输入数组之后，chars 的前 6 个字符应为 ["a","2","b","2","c","3"]。
 示例 2：

 输入：chars = ["a"]
 输出：1
 解释：唯一的组是 "a"，因为它是一个单独的字符，所以保持不变。
 在原地修改输入数组后，chars 的第一个字符应为 ["a"]。
 示例 3：

 输入：chars = ["a","b","b","b","b","b","b","b","b","b","b","b","b"]
 输出：4
 解释：分组是 "a" 和 "bbbbbbbbbbbb"，压缩为"ab12"。
 在对输入数组进行原地修改后，chars 的前 4 个字符应为 ["a","b","1","2"]。
 */
public class Compress {
    public int compress(char[] chars) {
        try {
            int read = 0;
            int write = 0;
            StringBuffer sb = new StringBuffer();
            while (read < chars.length) {
                int start = read;
                sb.append(chars[start]);
                while (read < chars.length && chars[read] == chars[start]) {
                    read++;
                }

                int count = read - start;
                if(count>1) {
                    sb.append(count);
                }
            }

            for (int i = 0; i < sb.toString().length(); i++) {
                chars[write++] = sb.toString().charAt(i);
            }
            return sb.length();
        } catch (Exception e) {
            throw new IllegalArgumentException("压缩算法错误");
        }
    }

    public int compress2(char[] chars) {
        StringBuffer buffer = new StringBuffer();
        int[] start = {0};
        IntStream.range(0, chars.length)
                .filter(i -> i == chars.length - 1 || chars[i] != chars[i + 1])
                .forEach(i -> {
                    int count = i - start[0] + 1;
                    buffer.append(chars[i]);
                    if (count > 1) {
                        buffer.append(count);
                    }
                    start[0] = i + 1;
                });

        char[] result = buffer.toString().toCharArray();
        System.arraycopy(result, 0, chars, 0, result.length);
        return result.length;
    }

    public int compress3(char[] chars) {
        int read = 0;
        int write = 0;
        while (read < chars.length) {
            char current = chars[read];
            int start = read;
            while (read < chars.length && chars[read] == current) {
                read++;
            }
            chars[write++] = current;
            int count = read - start;
            if (count > 1) {
                String countString = String.valueOf(count);
                for (int i = 0; i < countString.length(); i++) {
                    chars[write++] = countString.charAt(i);
                }
            }
        }
        return write;
    }
}
