package com.kuma.cloud.lab.spring.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.spring.domain.dto.PublishEventDTO;
import com.kuma.cloud.lab.spring.domain.vo.ArchitectureDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.ContextInfoVO;
import com.kuma.cloud.lab.spring.domain.vo.EventDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.IocDemoVO;
import com.kuma.cloud.lab.spring.domain.vo.SpringScenarioVO;
import com.kuma.cloud.lab.spring.service.SpringLabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Spring 核心机制学习")
@RestController
@RequestMapping("/lab/spring")
@RequiredArgsConstructor
public class SpringLabController {

    private final SpringLabService springLabService;

    @Operation(summary = "综合场景：IOC + 上下文 + 事件 + 分层架构")
    @PostMapping("/scenario")
    public Result<SpringScenarioVO> scenario() {
        return Result.success(springLabService.runScenario());
    }

    @Operation(summary = "IOC 演示：@Primary、@Qualifier、prototype 作用域")
    @GetMapping("/ioc")
    public Result<IocDemoVO> ioc() {
        return Result.success(springLabService.demonstrateIoc());
    }

    @Operation(summary = "ApplicationContext 演示：Bean 数量、Profile、Aware 回调")
    @GetMapping("/context")
    public Result<ContextInfoVO> context() {
        return Result.success(springLabService.demonstrateContext());
    }

    @Operation(summary = "事件演示：发布自定义事件并由 @EventListener 处理")
    @PostMapping("/events/publish")
    public Result<EventDemoVO> publishEvent(@Valid @RequestBody PublishEventDTO dto) {
        return Result.success(springLabService.publishUserRegisteredEvent(dto));
    }

    @Operation(summary = "架构演示：Controller → Service → Repository + 领域事件")
    @PostMapping("/architecture")
    public Result<ArchitectureDemoVO> architecture() {
        return Result.success(springLabService.demonstrateArchitecture());
    }

}
