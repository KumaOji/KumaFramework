package com.kuma.cloud.lab.auditlog;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "日志审计实验")
@RestController
@RequestMapping("/lab/audit-log")
public class AuditLogLabController {
    @Operation(summary = "完整审计实验：成功、失败、内容策略、代理边界与存储故障")
    @PostMapping("/scenario")
    public Result<AuditLogLearningDemo.Report> scenario() { return run("all"); }
    @Operation(summary = "成功审计、操作人及 SpEL 业务标识")
    @PostMapping("/success")
    public Result<AuditLogLearningDemo.Report> success() { return run("success"); }
    @Operation(summary = "失败审计与原业务异常保留")
    @PostMapping("/failure")
    public Result<AuditLogLearningDemo.Report> failure() { return run("failure"); }
    @Operation(summary = "敏感内容关闭与大文本截断")
    @PostMapping("/content")
    public Result<AuditLogLearningDemo.Report> content() { return run("content"); }
    @Operation(summary = "同对象自调用与代理调用对照")
    @PostMapping("/proxy")
    public Result<AuditLogLearningDemo.Report> proxy() { return run("proxy"); }
    @Operation(summary = "审计存储不可用时业务继续执行")
    @PostMapping("/storage")
    public Result<AuditLogLearningDemo.Report> storage() { return run("storage"); }
    private Result<AuditLogLearningDemo.Report> run(String lesson) {
        return Result.success(AuditLogLearningDemo.run(lesson));
    }
}
