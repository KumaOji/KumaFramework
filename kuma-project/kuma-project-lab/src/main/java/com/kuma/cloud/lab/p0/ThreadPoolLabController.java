package com.kuma.cloud.lab.p0;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "线程池与异步进阶实验")
@RestController
@RequestMapping("/lab/threadpool")
public class ThreadPoolLabController {
    @Operation(summary = "线程池完整实验：队列拒绝、异常、取消、饥饿与上下文清理")
    @PostMapping("/scenario")
    public Result<LabReport> scenario() throws Exception { return run("all"); }
    @Operation(summary = "有界队列、AbortPolicy 与 CallerRunsPolicy 对照")
    @PostMapping("/queue")
    public Result<LabReport> queue() throws Exception { return run("queue"); }
    @Operation(summary = "execute 未捕获异常与 submit Future 异常对照")
    @PostMapping("/exceptions")
    public Result<LabReport> exceptions() throws Exception { return run("exceptions"); }
    @Operation(summary = "get 超时不会取消任务；cancel(true) 与协作中断")
    @PostMapping("/cancellation")
    public Result<LabReport> cancellation() throws Exception { return run("cancellation"); }
    @Operation(summary = "同池嵌套等待导致饥饿与 CompletableFuture 非阻塞组合")
    @PostMapping("/starvation")
    public Result<LabReport> starvation() throws Exception { return run("starvation"); }
    @Operation(summary = "ThreadLocal 残留、MDC 快照传递与 finally 恢复")
    @PostMapping("/context")
    public Result<LabReport> context() throws Exception { return run("context"); }
    @Operation(summary = "慢依赖隔离、共享池阻塞、有界拒绝与CompletableFuture超时边界")
    @PostMapping("/bulkhead")
    public Result<LabReport> bulkhead() throws Exception { return run("bulkhead"); }
    private Result<LabReport> run(String lesson) throws Exception { return Result.success(ThreadPoolLearningDemo.run(lesson)); }
}
