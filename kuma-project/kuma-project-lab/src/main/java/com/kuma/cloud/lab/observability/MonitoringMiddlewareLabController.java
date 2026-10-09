package com.kuma.cloud.lab.observability;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "可观测性中间件实验")
@RestController
@RequestMapping("/lab/monitoring")
public class MonitoringMiddlewareLabController {
    @Operation(summary = "查看各组件 UI 地址与页面位置（Collector 无独立业务 UI）")
    @GetMapping("/guide")
    public Result<List<MonitoringMiddlewareLabs.UiLink>> guide() {
        try (var labs = create()) { return Result.success(labs.uiLinks()); }
    }
    @Operation(summary = "Loki：写入独立日志并用 LogQL 查回")
    @PostMapping("/loki")
    public Result<MonitoringMiddlewareLabs.Report> loki() throws Exception { return run("loki"); }
    @Operation(summary = "Prometheus：up 即时查询与范围查询对照")
    @PostMapping("/prometheus")
    public Result<MonitoringMiddlewareLabs.Report> prometheus() throws Exception { return run("prometheus"); }
    @Operation(summary = "Alertmanager：发送一分钟实验告警并查回")
    @PostMapping("/alertmanager")
    public Result<MonitoringMiddlewareLabs.Report> alertmanager() throws Exception { return run("alertmanager"); }
    @Operation(summary = "OTel：SDK 导出至 Collector 并验证存储证据")
    @PostMapping("/otel")
    public Result<MonitoringMiddlewareLabs.Report> otel() throws Exception { return run("otel"); }
    @Operation(summary = "SkyWalking：逐个查回 trace 的六个关联 span")
    @PostMapping("/skywalking")
    public Result<MonitoringMiddlewareLabs.Report> skywalking() throws Exception { return run("skywalking"); }
    @Operation(summary = "Grafana：健康、登录边界、三服务面板和数据源检查")
    @PostMapping("/grafana")
    public Result<MonitoringMiddlewareLabs.Report> grafana() throws Exception { return run("grafana"); }

    private Result<MonitoringMiddlewareLabs.Report> run(String component) throws Exception {
        try (var labs = create()) { return Result.success(labs.run(component)); }
    }
    private MonitoringMiddlewareLabs create() {
        return new MonitoringMiddlewareLabs(MonitoringMiddlewareLabs.Endpoints.local(),
                System.getenv().getOrDefault("KUMA_LAB_MONITORING_GRAFANA_USER", "admin"),
                System.getenv().getOrDefault("KUMA_LAB_MONITORING_GRAFANA_PASSWORD", ""));
    }
}
