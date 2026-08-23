package com.kuma.cloud.lab.leetcode.config;

import com.kuma.cloud.lab.leetcode.runner.LeetCodeProblemRunner;
import com.kuma.cloud.lab.leetcode.runner.LeetCodeTestCase;
import com.kuma.cloud.lab.leetcode.support.LeetCodeCompareUtils;
import com.kuma.cloud.lab.leetcode.support.LeetCodeInputUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class LeetCodeBasicProblemRunnersConfiguration {

    @Bean
    List<LeetCodeProblemRunner> leetCodeBasicProblemRunners() {
        return List.of(
                runner(1, "两数之和", "EASY", List.of("array", "hash"),
                        input -> new com.kuma.cloud.leetcode.p0001.Solution().twoSum(
                                LeetCodeInputUtils.intArray(input, "nums"),
                                LeetCodeInputUtils.intValue(input, "target")),
                        tc("example1", Map.of("nums", List.of(2, 7, 11, 15), "target", 9), List.of(0, 1)),
                        tc("example2", Map.of("nums", List.of(3, 2, 4), "target", 6), List.of(1, 2))
                ),
                runner(3, "无重复字符的最长子串", "MEDIUM", List.of("string", "sliding-window"),
                        input -> new com.kuma.cloud.leetcode.p0003.Solution().lengthOfLongestSubstring(
                                LeetCodeInputUtils.stringValue(input, "s")),
                        tc("example1", Map.of("s", "abcabcbb"), 3),
                        tc("example2", Map.of("s", "bbbbb"), 1)
                ),
                runner(15, "三数之和", "MEDIUM", List.of("array", "two-pointers"),
                        input -> LeetCodeCompareUtils.sortNestedIntegerLists(
                                new com.kuma.cloud.leetcode.p0015.Solution().threeSum(
                                        LeetCodeInputUtils.intArray(input, "nums"))),
                        tc("example1", Map.of("nums", List.of(-1, 0, 1, 2, -1, -4)),
                                List.of(List.of(-1, -1, 2), List.of(-1, 0, 1)))
                ),
                runner(20, "有效的括号", "EASY", List.of("string", "stack"),
                        input -> new com.kuma.cloud.leetcode.p0020.Solution().isValid(
                                LeetCodeInputUtils.stringValue(input, "s")),
                        tc("example1", Map.of("s", "()"), true),
                        tc("invalid", Map.of("s", "(]"), false)
                ),
                runner(42, "接雨水", "HARD", List.of("array", "two-pointers"),
                        input -> new com.kuma.cloud.leetcode.p0042.Solution().trap(
                                LeetCodeInputUtils.intArray(input, "height")),
                        tc("example1", Map.of("height", List.of(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)), 6)
                ),
                runner(53, "最大子数组和", "MEDIUM", List.of("array", "dp"),
                        input -> new com.kuma.cloud.leetcode.p0053.Solution().maxSubArray(
                                LeetCodeInputUtils.intArray(input, "nums")),
                        tc("example1", Map.of("nums", List.of(-2, 1, -3, 4, -1, 2, 1, -5, 4)), 6)
                ),
                runner(56, "合并区间", "MEDIUM", List.of("array", "sort"),
                        input -> Arrays.stream(new com.kuma.cloud.leetcode.p0056.Solution().merge(
                                        LeetCodeInputUtils.intMatrix(input, "intervals")))
                                .map(interval -> List.of(interval[0], interval[1]))
                                .toList(),
                        tc("example1", Map.of("intervals", List.of(List.of(1, 3), List.of(2, 6), List.of(8, 10), List.of(15, 18))),
                                List.of(List.of(1, 6), List.of(8, 10), List.of(15, 18)))
                ),
                runner(70, "爬楼梯", "EASY", List.of("dp"),
                        input -> new com.kuma.cloud.leetcode.p0070.Solution().climbStairs(
                                LeetCodeInputUtils.intValue(input, "n")),
                        tc("example1", Map.of("n", 3), 3),
                        tc("example2", Map.of("n", 5), 8)
                ),
                runner(200, "岛屿数量", "MEDIUM", List.of("matrix", "dfs"),
                        input -> new com.kuma.cloud.leetcode.p0200.Solution().numIslands(
                                LeetCodeInputUtils.charMatrix(input, "grid")),
                        tc("example1", Map.of("grid", List.of("11110", "11010", "11000", "00000")), 1)
                ),
                runner(215, "数组中的第 K 个最大元素", "MEDIUM", List.of("array", "heap"),
                        input -> new com.kuma.cloud.leetcode.p0215.Solution().findKthLargest(
                                LeetCodeInputUtils.intArray(input, "nums"),
                                LeetCodeInputUtils.intValue(input, "k")),
                        tc("example1", Map.of("nums", List.of(3, 2, 1, 5, 6, 4), "k", 2), 5)
                ),
                runner(238, "除自身以外数组的乘积", "MEDIUM", List.of("array", "prefix-sum"),
                        input -> new com.kuma.cloud.leetcode.p0238.Solution().productExceptSelf(
                                LeetCodeInputUtils.intArray(input, "nums")),
                        tc("example1", Map.of("nums", List.of(1, 2, 3, 4)), List.of(24, 12, 8, 6))
                ),
                runner(239, "滑动窗口最大值", "HARD", List.of("array", "deque"),
                        input -> new com.kuma.cloud.leetcode.p0239.Solution().maxSlidingWindow(
                                LeetCodeInputUtils.intArray(input, "nums"),
                                LeetCodeInputUtils.intValue(input, "k")),
                        tc("example1", Map.of("nums", List.of(1, 3, -1, -3, 5, 3, 6, 7), "k", 3),
                                List.of(3, 3, 5, 5, 6, 7))
                ),
                runner(283, "移动零", "EASY", List.of("array", "two-pointers"),
                        input -> {
                            int[] nums = LeetCodeInputUtils.intArray(input, "nums");
                            new com.kuma.cloud.leetcode.p0283.Solution().moveZeroes(nums);
                            return nums;
                        },
                        tc("example1", Map.of("nums", List.of(0, 1, 0, 3, 12)), List.of(1, 3, 12, 0, 0))
                ),
                runner(322, "零钱兑换", "MEDIUM", List.of("dp"),
                        input -> new com.kuma.cloud.leetcode.p0322.Solution().coinChange(
                                LeetCodeInputUtils.intArray(input, "coins"),
                                LeetCodeInputUtils.intValue(input, "amount")),
                        tc("example1", Map.of("coins", List.of(1, 2, 5), "amount", 11), 3),
                        tc("impossible", Map.of("coins", List.of(2), "amount", 3), -1)
                ),
                runner(347, "前 K 个高频元素", "MEDIUM", List.of("array", "hash", "heap"),
                        input -> {
                            int[] result = new com.kuma.cloud.leetcode.p0347.Solution().topKFrequent(
                                    LeetCodeInputUtils.intArray(input, "nums"),
                                    LeetCodeInputUtils.intValue(input, "k"));
                            Arrays.sort(result);
                            return result;
                        },
                        tc("example1", Map.of("nums", List.of(1, 1, 1, 2, 2, 3), "k", 2), List.of(1, 2))
                ),
                runner(443, "压缩字符串", "MEDIUM", List.of("string", "two-pointers"),
                        input -> {
                            char[] chars = LeetCodeInputUtils.charArray(input, "chars");
                            int length = new com.kuma.cloud.leetcode.p0443.Solution().compress(chars);
                            return Map.of("length", length, "chars", new String(chars, 0, length));
                        },
                        tc("example1", Map.of("chars", "aabcccccaaa"),
                                Map.of("length", 9, "chars", "a2b1c5a3"))
                ),
                runner(1679, "K 和数对的最大数目", "MEDIUM", List.of("array", "hash"),
                        input -> new com.kuma.cloud.leetcode.p1679.Solution().maxOperations(
                                LeetCodeInputUtils.intArray(input, "nums"),
                                LeetCodeInputUtils.intValue(input, "k")),
                        tc("example1", Map.of("nums", List.of(1, 2, 3, 4), "k", 5), 2)
                ),
                runner(1768, "交替合并字符串", "EASY", List.of("string"),
                        input -> new com.kuma.cloud.leetcode.p1768.Solution().mergeAlternately(
                                LeetCodeInputUtils.stringValue(input, "word1"),
                                LeetCodeInputUtils.stringValue(input, "word2")),
                        tc("example1", Map.of("word1", "abc", "word2", "pqr"), "apbqcr")
                )
        );
    }

    private static LeetCodeProblemRunner runner(
            int number,
            String title,
            String difficulty,
            List<String> tags,
            Function<Map<String, Object>, Object> solver,
            LeetCodeTestCase... cases
    ) {
        return new LeetCodeProblemRunner.FunctionalLeetCodeProblemRunner(
                number, title, difficulty, tags, solver, List.of(cases));
    }

    private static LeetCodeTestCase tc(String name, Map<String, Object> input, Object expected) {
        return new LeetCodeTestCase(name, input, expected);
    }

}
