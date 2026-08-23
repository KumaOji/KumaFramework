package com.kuma.cloud.lab.leetcode.domain.vo;

import java.util.List;

/**
 * LeetCode 题目摘要。
 */
public record LeetCodeProblemVO(
        int number,
        String title,
        String difficulty,
        List<String> tags,
        int testCaseCount
) {
}
