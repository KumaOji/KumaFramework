package com.kuma.cloud.lab.leetcode.config;

import com.kuma.cloud.lab.leetcode.runner.LeetCodeProblemRunner;
import com.kuma.cloud.lab.leetcode.support.LeetCodeCompareUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeetCodeProblemRunnersConfigurationTest {

    @Test
    void newProblemsAreRegisteredAndTheirBuiltInCasesPass() {
        List<LeetCodeProblemRunner> runners = new LeetCodeProblemRunnersConfiguration()
                .leetCodeProblemRunners(
                        new LeetCodeBasicProblemRunnersConfiguration().leetCodeBasicProblemRunners(),
                        new LeetCodeStructureProblemRunnersConfiguration().leetCodeStructureProblemRunners());
        Map<Integer, LeetCodeProblemRunner> byNumber = runners.stream()
                .collect(Collectors.toMap(LeetCodeProblemRunner::number, Function.identity()));
        assertEquals(29, byNumber.size());
        for (int number : List.of(11, 33, 121, 198, 704)) {
            LeetCodeProblemRunner runner = byNumber.get(number);
            assertTrue(runner != null, "Missing problem " + number);
            assertEquals(4, runner.testCases().size());
            runner.testCases().forEach(testCase -> assertTrue(
                    LeetCodeCompareUtils.equals(testCase.expected(), runner.solve(testCase.input())),
                    "Problem " + number + ": " + testCase.name()));
        }
    }
}
