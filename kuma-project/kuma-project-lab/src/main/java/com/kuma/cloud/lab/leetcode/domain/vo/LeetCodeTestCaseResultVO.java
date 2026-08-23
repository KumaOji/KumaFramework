package com.kuma.cloud.lab.leetcode.domain.vo;

import java.util.List;

/**
 * 单条测试用例执行结果。
 */
public record LeetCodeTestCaseResultVO(
        String name,
        boolean passed,
        Object expected,
        Object actual,
        String error
) {
}
