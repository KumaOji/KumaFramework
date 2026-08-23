package com.kuma.cloud.lab.leetcode.service.impl;

import com.kuma.cloud.lab.leetcode.domain.dto.LeetCodeRunDTO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeProblemResultVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeProblemVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeRunVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeScenarioVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeTestCaseResultVO;
import com.kuma.cloud.lab.leetcode.runner.LeetCodeProblemRunner;
import com.kuma.cloud.lab.leetcode.runner.LeetCodeTestCase;
import com.kuma.cloud.lab.leetcode.service.LeetCodeTestService;
import com.kuma.cloud.lab.leetcode.support.LeetCodeCompareUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeetCodeTestServiceImpl implements LeetCodeTestService {

    private final List<LeetCodeProblemRunner> runners;

    @Override
    public List<LeetCodeProblemVO> listProblems() {
        return runners.stream()
                .sorted(Comparator.comparingInt(LeetCodeProblemRunner::number))
                .map(this::toProblemVO)
                .toList();
    }

    @Override
    public LeetCodeProblemVO getProblem(int number) {
        return toProblemVO(requireRunner(number));
    }

    @Override
    public LeetCodeRunVO run(int number, LeetCodeRunDTO dto) {
        LeetCodeProblemRunner runner = requireRunner(number);
        Object output = runner.solve(dto.getInput());
        return new LeetCodeRunVO(number, runner.title(), LeetCodeCompareUtils.normalize(output));
    }

    @Override
    public LeetCodeScenarioVO runScenario() {
        List<LeetCodeProblemResultVO> results = runners.stream()
                .sorted(Comparator.comparingInt(LeetCodeProblemRunner::number))
                .map(this::evaluateProblem)
                .toList();
        return summarize(results);
    }

    @Override
    public LeetCodeScenarioVO runProblemTests(int number) {
        return summarize(List.of(evaluateProblem(requireRunner(number))));
    }

    private LeetCodeScenarioVO summarize(List<LeetCodeProblemResultVO> results) {
        int passedProblems = (int) results.stream().filter(LeetCodeProblemResultVO::passed).count();
        int totalCases = results.stream().mapToInt(result -> result.cases().size()).sum();
        int passedCases = results.stream()
                .flatMap(result -> result.cases().stream())
                .mapToInt(testCase -> testCase.passed() ? 1 : 0)
                .sum();
        return new LeetCodeScenarioVO(results.size(), passedProblems, totalCases, passedCases, results);
    }

    private LeetCodeProblemResultVO evaluateProblem(LeetCodeProblemRunner runner) {
        List<LeetCodeTestCaseResultVO> cases = new ArrayList<>();
        for (LeetCodeTestCase testCase : runner.testCases()) {
            cases.add(evaluateCase(runner, testCase));
        }
        boolean passed = cases.stream().allMatch(LeetCodeTestCaseResultVO::passed);
        return new LeetCodeProblemResultVO(runner.number(), runner.title(), passed, cases);
    }

    private LeetCodeTestCaseResultVO evaluateCase(LeetCodeProblemRunner runner, LeetCodeTestCase testCase) {
        try {
            Object actual = runner.solve(testCase.input());
            boolean passed = LeetCodeCompareUtils.equals(testCase.expected(), actual);
            return new LeetCodeTestCaseResultVO(
                    testCase.name(),
                    passed,
                    LeetCodeCompareUtils.normalize(testCase.expected()),
                    LeetCodeCompareUtils.normalize(actual),
                    passed ? null : "输出与期望不一致"
            );
        } catch (Exception ex) {
            return new LeetCodeTestCaseResultVO(
                    testCase.name(),
                    false,
                    LeetCodeCompareUtils.normalize(testCase.expected()),
                    null,
                    ex.getMessage()
            );
        }
    }

    private LeetCodeProblemVO toProblemVO(LeetCodeProblemRunner runner) {
        return new LeetCodeProblemVO(
                runner.number(),
                runner.title(),
                runner.difficulty(),
                runner.tags(),
                runner.testCases().size()
        );
    }

    private LeetCodeProblemRunner requireRunner(int number) {
        Map<Integer, LeetCodeProblemRunner> runnerMap = runners.stream()
                .collect(Collectors.toMap(LeetCodeProblemRunner::number, Function.identity(), (left, right) -> left));
        LeetCodeProblemRunner runner = runnerMap.get(number);
        if (runner == null) {
            throw new IllegalArgumentException("未找到题号为 " + number + " 的题目");
        }
        return runner;
    }

}
