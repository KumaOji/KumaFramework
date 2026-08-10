package com.kuma.cloud.lab.jni.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.jni.domain.dto.JniBinaryOpDTO;
import com.kuma.cloud.lab.jni.domain.dto.JniGreetDTO;
import com.kuma.cloud.lab.jni.domain.dto.JniSumDTO;
import com.kuma.cloud.lab.jni.domain.vo.JniScenarioVO;
import com.kuma.cloud.lab.jni.domain.vo.JniValueVO;
import com.kuma.cloud.lab.jni.service.JniTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JNI 测试")
@RestController
@RequestMapping("/lab/jni")
@RequiredArgsConstructor
public class JniTestController {

    private final JniTestService jniTestService;

    @Operation(summary = "执行 add / multiply / greet / sumArray 场景测试")
    @PostMapping("/scenario")
    public Result<JniScenarioVO> scenario() {
        return Result.success(jniTestService.runScenario());
    }

    @Operation(summary = "调用 C 实现整数加法")
    @PostMapping("/add")
    public Result<JniValueVO> add(@Valid @RequestBody JniBinaryOpDTO dto) {
        return Result.success(jniTestService.add(dto));
    }

    @Operation(summary = "调用 C 实现整数乘法")
    @PostMapping("/multiply")
    public Result<JniValueVO> multiply(@Valid @RequestBody JniBinaryOpDTO dto) {
        return Result.success(jniTestService.multiply(dto));
    }

    @Operation(summary = "调用 C 实现字符串问候")
    @PostMapping("/greet")
    public Result<JniValueVO> greet(@Valid @RequestBody JniGreetDTO dto) {
        return Result.success(jniTestService.greet(dto));
    }

    @Operation(summary = "调用 C 实现数组求和")
    @PostMapping("/sum")
    public Result<JniValueVO> sum(@Valid @RequestBody JniSumDTO dto) {
        return Result.success(jniTestService.sum(dto));
    }

}
