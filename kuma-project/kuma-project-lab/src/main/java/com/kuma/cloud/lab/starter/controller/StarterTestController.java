package com.kuma.cloud.lab.starter.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.starter.domain.vo.StarterCatalogItemVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterProbeResultVO;
import com.kuma.cloud.lab.starter.domain.vo.StarterScenarioVO;
import com.kuma.cloud.lab.starter.service.StarterTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Starter 测试")
@RestController
@RequestMapping("/lab/starter")
@RequiredArgsConstructor
public class StarterTestController {

    private final StarterTestService starterTestService;

    @Operation(summary = "获取全部 Starter 目录（含 classpath 探测结果）")
    @GetMapping("/catalog")
    public Result<List<StarterCatalogItemVO>> catalog() {
        return Result.success(starterTestService.catalog());
    }

    @Operation(summary = "获取当前已引入 classpath 的 Starter 列表")
    @GetMapping("/catalog/on-classpath")
    public Result<List<StarterCatalogItemVO>> catalogOnClasspath() {
        return Result.success(starterTestService.catalogOnClasspath());
    }

    @Operation(summary = "获取已注册探测器的诊断结果")
    @GetMapping("/probes")
    public Result<List<StarterProbeResultVO>> probes() {
        return Result.success(starterTestService.probes());
    }

    @Operation(summary = "诊断指定 Starter")
    @GetMapping("/{starterName}/diagnose")
    public Result<StarterProbeResultVO> diagnose(@PathVariable String starterName) {
        return Result.success(starterTestService.diagnose(starterName));
    }

    @Operation(summary = "对指定 Starter 执行冒烟测试")
    @PostMapping("/{starterName}/smoke-test")
    public Result<StarterProbeResultVO> smokeTest(@PathVariable String starterName) {
        return Result.success(starterTestService.smokeTest(starterName));
    }

    @Operation(summary = "执行全部已注册 Starter 的场景测试")
    @PostMapping("/scenario")
    public Result<StarterScenarioVO> scenario() {
        return Result.success(starterTestService.runScenario());
    }

}
