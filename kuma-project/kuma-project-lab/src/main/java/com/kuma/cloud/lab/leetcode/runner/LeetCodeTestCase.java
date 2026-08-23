package com.kuma.cloud.lab.leetcode.runner;

import java.util.Map;

/**
 * LeetCode 内置测试用例。
 */
public record LeetCodeTestCase(
        String name,
        Map<String, Object> input,
        Object expected
) {
}
