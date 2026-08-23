package com.kuma.cloud.lab.leetcode.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.leetcode.domain.dto.LeetCodeRunDTO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeProblemVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeRunVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeScenarioVO;
import com.kuma.cloud.lab.leetcode.service.LeetCodeTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "LeetCode 练习")
@RestController
@RequestMapping("/lab/leetcode")
@RequiredArgsConstructor
public class LeetCodeTestController {

    private final LeetCodeTestService leetCodeTestService;

    @Operation(summary = "获取全部练习题列表")
    @GetMapping("/problems")
    public Result<List<LeetCodeProblemVO>> problems() {
        return Result.success(leetCodeTestService.listProblems());
    }

    @Operation(summary = "获取指定题号题目信息")
    @GetMapping("/problems/{number}")
    public Result<LeetCodeProblemVO> problem(@PathVariable int number) {
        return Result.success(leetCodeTestService.getProblem(number));
    }

    @Operation(summary = "运行指定题目")
    @PostMapping("/run/{number}")
    public Result<LeetCodeRunVO> run(@PathVariable int number, @Valid @RequestBody LeetCodeRunDTO dto) {
        return Result.success(leetCodeTestService.run(number, dto));
    }

    @Operation(summary = "运行全部题目内置测试用例")
    @PostMapping("/scenario")
    public Result<LeetCodeScenarioVO> scenario() {
        return Result.success(leetCodeTestService.runScenario());
    }

    @Operation(summary = "运行指定题目内置测试用例")
    @PostMapping("/problems/{number}/test")
    public Result<LeetCodeScenarioVO> testProblem(@PathVariable int number) {
        return Result.success(leetCodeTestService.runProblemTests(number));
    }

}
