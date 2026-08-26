package com.kuma.cloud.lab.snowflake.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.snowflake.domain.dto.SnowflakeGenerateDTO;
import com.kuma.cloud.lab.snowflake.domain.vo.SnowflakeGenerateVO;
import com.kuma.cloud.lab.snowflake.domain.vo.SnowflakeScenarioVO;
import com.kuma.cloud.lab.snowflake.service.SnowflakeTestService;
import com.kuma.cloud.lab.snowflake.support.SnowflakeIdParser.ParsedSnowflakeId;
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

@Tag(name = "雪花算法测试")
@RestController
@RequestMapping("/lab/snowflake")
@RequiredArgsConstructor
public class SnowflakeTestController {

    private final SnowflakeTestService snowflakeTestService;

    @Operation(summary = "执行雪花算法场景测试（批量生成、唯一性、递增性）")
    @PostMapping("/scenario")
    public Result<SnowflakeScenarioVO> scenario() {
        return Result.success(snowflakeTestService.runScenario());
    }

    @Operation(summary = "批量生成雪花 ID 并解析各字段")
    @PostMapping("/generate")
    public Result<SnowflakeGenerateVO> generate(@Valid @RequestBody SnowflakeGenerateDTO dto) {
        return Result.success(snowflakeTestService.generate(dto));
    }

    @Operation(summary = "解析已有雪花 ID 的时间戳、机器位与序列号")
    @GetMapping("/parse/{id}")
    public Result<ParsedSnowflakeId> parse(@PathVariable long id) {
        return Result.success(snowflakeTestService.parse(id));
    }

}
