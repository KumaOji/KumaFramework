package com.kuma.cloud.lab.leetcode.domain.vo;

/**
 * 单次 LeetCode 运行结果。
 */
public record LeetCodeRunVO(
        int number,
        String title,
        Object output
) {
}
