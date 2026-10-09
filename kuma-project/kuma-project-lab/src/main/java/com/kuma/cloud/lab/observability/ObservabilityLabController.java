package com.kuma.cloud.lab.observability;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "OTel 与 SkyWalking 学习")
@RestController
@RequestMapping("/lab/observability")
public class ObservabilityLabController {

    @Operation(summary = "实验路线与现有 Collector 的功能边界")
    @GetMapping("/guide")
    public Result<Map<String, Object>> guide() {
        return Result.success(Map.of(
                "otel", "API/SDK 采集 traces、metrics、logs；Collector 接收、处理和转发；后端负责查询。",
                "skywalking", "Java Agent 自动增强与 toolkit 埋点；OAP 分析/存储；UI 查询服务、拓扑和追踪。",
                "offline", "POST /lab/observability/otel：真实 SDK 本地证据和断言，不发送网络遥测。",
                "live", "Gradle otelLab -PotelExport=true：OTLP 导出，并从 OAP 按 ID 查回六个关联 span。",
                "agent", "挂载兼容 Agent 后调用 skywalking/normal、slow、error，在原生追踪页面比较。",
                "pipeline", "仓库 Collector 仅启用 traces；本实验 metrics/logs 仅在 SDK 内存导出，不会上报 SkyWalking。",
                "docs", "kuma-project/kuma-project-lab/docs/observability-labs.md"));
    }

    @Operation(summary = "OTel SDK：span、W3C 传播、异步断链、采样、指标和日志关联")
    @PostMapping("/otel")
    public Result<OtelLearningDemo.Report> otel() throws Exception {
        return Result.success(OtelLearningDemo.run(null));
    }

    @Operation(summary = "SkyWalking：正常调用与自定义 span")
    @PostMapping("/skywalking/normal")
    public Result<SkyWalkingLearningDemo.Report> normal() throws InterruptedException {
        return Result.success(SkyWalkingLearningDemo.checkout("normal"));
    }

    @Operation(summary = "SkyWalking：200ms 模拟慢调用定位")
    @PostMapping("/skywalking/slow")
    public Result<SkyWalkingLearningDemo.Report> slow() throws InterruptedException {
        return Result.success(SkyWalkingLearningDemo.checkout("slow"));
    }

    @Operation(summary = "SkyWalking：异常事件与错误 span，模拟失败被捕获后返回 HTTP 200")
    @PostMapping("/skywalking/error")
    public Result<SkyWalkingLearningDemo.Report> error() throws InterruptedException {
        return Result.success(SkyWalkingLearningDemo.checkout("error"));
    }
}
