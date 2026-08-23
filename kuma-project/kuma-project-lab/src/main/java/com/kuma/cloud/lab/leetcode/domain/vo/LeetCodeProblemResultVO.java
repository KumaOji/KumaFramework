package com.kuma.cloud.lab.leetcode.domain.vo;

import java.util.List;

/**
 * 单题测试汇总。
 */
public record LeetCodeProblemResultVO(
        int number,
        String title,
        boolean passed,
        List<LeetCodeTestCaseResultVO> cases
) {
}
