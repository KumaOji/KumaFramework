package com.kuma.cloud.lab.bloom.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.bloom.domain.dto.BloomContainsDTO;
import com.kuma.cloud.lab.bloom.domain.vo.BloomContainsVO;
import com.kuma.cloud.lab.bloom.domain.vo.BloomScenarioVO;
import com.kuma.cloud.lab.bloom.service.BloomTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "布隆过滤器测试")
@RestController
@RequestMapping("/lab/bloom")
@RequiredArgsConstructor
public class BloomTestController {

    private final BloomTestService bloomTestService;

    @Operation(summary = "执行布隆过滤器场景测试（插入、命中、假阳性演示）")
    @PostMapping("/scenario")
    public Result<BloomScenarioVO> scenario() {
        return Result.success(bloomTestService.runScenario());
    }

    @Operation(summary = "向实例布隆过滤器添加元素")
    @PostMapping("/add/{value}")
    public Result<BloomContainsVO> add(@PathVariable String value) {
        return Result.success(bloomTestService.add(value));
    }

    @Operation(summary = "查询元素是否可能存在")
    @PostMapping("/contains")
    public Result<BloomContainsVO> contains(@Valid @RequestBody BloomContainsDTO dto) {
        return Result.success(bloomTestService.contains(dto));
    }

}
