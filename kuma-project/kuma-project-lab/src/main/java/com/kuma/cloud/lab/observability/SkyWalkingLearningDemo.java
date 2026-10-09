package com.kuma.cloud.lab.observability;

import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Toolkit 注解由 Java Agent 增强，单独引入依赖不会采集。
 * 慢操作和支付异常均为教学模拟，不调用数据库、不创建真实订单。
 */
public final class SkyWalkingLearningDemo {
    private SkyWalkingLearningDemo() { }

    public record Report(String scenario, String traceId, boolean activeTrace, double elapsedMs,
                         String outcome, List<String> expectedSpans, String observation) { }

    public static void main(String[] args) throws Exception {
        boolean requireAgent = args.length == 1 && args[0].equals("--require-agent");
        if (args.length > 0 && !requireAgent) throw new IllegalArgumentException("Usage: SkyWalkingLearningDemo [--require-agent]");
        if (requireAgent) Thread.sleep(3000); // 等待 Agent 与本地 OAP 建立连接，减少首条 trace 的启动丢失。
        List<Report> reports = List.of(checkout("normal"), checkout("slow"), checkout("error"));
        System.out.println(JsonMapper.builder().build().writerWithDefaultPrettyPrinter().writeValueAsString(reports));
        if (requireAgent && reports.stream().anyMatch(report -> !report.activeTrace())) {
            throw new IllegalStateException("Agent 未增强 toolkit；检查 Agent/JDK 兼容性和 apm-toolkit-trace 插件。");
        }
        if (requireAgent) Thread.sleep(5000); // 给 Agent 周期上报留下时间；实际存储仍需 UI 验证。
    }

    @Trace(operationName = "lab.skywalking.checkout")
    public static Report checkout(String scenario) throws InterruptedException {
        if (!List.of("normal", "slow", "error").contains(scenario)) {
            throw new IllegalArgumentException("scenario must be normal, slow or error");
        }
        long start = System.nanoTime();
        ActiveSpan.tag("lab.scenario", scenario);
        ActiveSpan.tag("lab.synthetic", "true");
        String outcome = "SUCCESS";
        inventory(scenario.equals("slow"));
        try {
            payment(scenario.equals("error"));
        } catch (IllegalStateException failure) {
            ActiveSpan.error(failure);
            outcome = "SIMULATED_FAILURE";
        }
        String traceId = TraceContext.traceId();
        boolean active = traceId != null && !traceId.isBlank() && !traceId.equals("Ignored_Trace") && !traceId.equals("N/A");
        System.getLogger(SkyWalkingLearningDemo.class.getName()).log(System.Logger.Level.INFO,
                "skywalking traceId=" + traceId + " scenario=" + scenario + " outcome=" + outcome);
        return new Report(scenario, traceId, active, (System.nanoTime() - start) / 1_000_000.0, outcome,
                List.of("lab.skywalking.checkout", "lab.skywalking.inventory", "lab.skywalking.payment"),
                active ? "当前有 native trace；请在 SkyWalking 原生追踪页查回，才能确认上报与存储。"
                        : "无有效 native trace：未挂载 Agent、toolkit 未增强或当前采样未保留；业务模拟仍可运行。");
    }

    @Trace(operationName = "lab.skywalking.inventory")
    private static void inventory(boolean slow) throws InterruptedException {
        ActiveSpan.tag("lab.simulated.delay_ms", slow ? "200" : "5");
        Thread.sleep(slow ? 200 : 5);
    }

    @Trace(operationName = "lab.skywalking.payment")
    private static void payment(boolean fail) {
        if (fail) {
            IllegalStateException failure = new IllegalStateException("synthetic payment rejected");
            ActiveSpan.error(failure);
            throw failure;
        }
        ActiveSpan.info("synthetic payment accepted");
    }
}
