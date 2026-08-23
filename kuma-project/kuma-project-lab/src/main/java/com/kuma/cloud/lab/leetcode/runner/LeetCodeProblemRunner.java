package com.kuma.cloud.lab.leetcode.runner;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * LeetCode 题目运行器：封装题解调用与内置测试用例。
 */
public interface LeetCodeProblemRunner {

    int number();

    String title();

    String difficulty();

    List<String> tags();

    Object solve(Map<String, Object> input);

    List<LeetCodeTestCase> testCases();

    /**
     * 函数式运行器实现，便于在配置类中集中注册题目。
     */
    record FunctionalLeetCodeProblemRunner(
            int number,
            String title,
            String difficulty,
            List<String> tags,
            Function<Map<String, Object>, Object> solver,
            List<LeetCodeTestCase> testCases
    ) implements LeetCodeProblemRunner {

        @Override
        public Object solve(Map<String, Object> input) {
            return solver.apply(input);
        }
    }

}
