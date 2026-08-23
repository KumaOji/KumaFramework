package com.kuma.cloud.lab.leetcode.domain.vo;

import java.util.List;

/**
 * LeetCode 场景测试汇总。
 */
public record LeetCodeScenarioVO(
        int totalProblems,
        int passedProblems,
        int totalCases,
        int passedCases,
        List<LeetCodeProblemResultVO> problems
) {
}
