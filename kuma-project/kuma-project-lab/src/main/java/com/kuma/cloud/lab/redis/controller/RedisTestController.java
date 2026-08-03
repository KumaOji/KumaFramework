package com.kuma.cloud.lab.redis.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.redis.domain.dto.RedisHashWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisListWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisSetWriteDTO;
import com.kuma.cloud.lab.redis.domain.dto.RedisStringWriteDTO;
import com.kuma.cloud.lab.redis.domain.vo.RedisScenarioVO;
import com.kuma.cloud.lab.redis.domain.vo.RedisValueVO;
import com.kuma.cloud.lab.redis.service.RedisTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Redis 测试")
@RestController
@RequestMapping("/lab/redis")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTestService redisTestService;

    @Operation(summary = "执行 String / Hash / List / Set 场景测试并返回逐步结果")
    @PostMapping("/scenario")
    public Result<RedisScenarioVO> scenario() {
        return Result.success(redisTestService.runScenario());
    }

    @Operation(summary = "写入 String 值，可选 TTL")
    @PostMapping("/string")
    public Result<RedisValueVO> writeString(@Valid @RequestBody RedisStringWriteDTO dto) {
        return Result.success(redisTestService.writeString(dto));
    }

    @Operation(summary = "读取 String 值")
    @GetMapping("/string/{key}")
    public Result<RedisValueVO> readString(@PathVariable String key) {
        return Result.success(redisTestService.readString(key));
    }

    @Operation(summary = "写入 Hash 字段")
    @PostMapping("/hash")
    public Result<RedisValueVO> writeHash(@Valid @RequestBody RedisHashWriteDTO dto) {
        return Result.success(redisTestService.writeHash(dto));
    }

    @Operation(summary = "读取 Hash 全部字段")
    @GetMapping("/hash/{key}")
    public Result<RedisValueVO> readHash(@PathVariable String key) {
        return Result.success(redisTestService.readHash(key));
    }

    @Operation(summary = "向 List 追加元素")
    @PostMapping("/list")
    public Result<RedisValueVO> writeList(@Valid @RequestBody RedisListWriteDTO dto) {
        return Result.success(redisTestService.writeList(dto));
    }

    @Operation(summary = "读取 List 全部元素")
    @GetMapping("/list/{key}")
    public Result<RedisValueVO> readList(@PathVariable String key) {
        return Result.success(redisTestService.readList(key));
    }

    @Operation(summary = "向 Set 添加成员")
    @PostMapping("/set")
    public Result<RedisValueVO> writeSet(@Valid @RequestBody RedisSetWriteDTO dto) {
        return Result.success(redisTestService.writeSet(dto));
    }

    @Operation(summary = "读取 Set 全部成员")
    @GetMapping("/set/{key}")
    public Result<RedisValueVO> readSet(@PathVariable String key) {
        return Result.success(redisTestService.readSet(key));
    }

    @Operation(summary = "删除指定 key")
    @DeleteMapping("/{key}")
    public Result<Long> delete(@PathVariable String key) {
        return Result.success(redisTestService.delete(key));
    }

}
