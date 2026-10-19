package com.kuma.cloud.lab.session;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "HTTP Session 实验")
@RestController
@RequestMapping("/lab/session")
public class SessionLabController {
    @Operation(summary = "Session 完整实验：Cookie 延续、客户端隔离、TCP连接变化、ID更换、注销与过期")
    @PostMapping("/scenario")
    public Result<SessionLearningDemo.Report> scenario() throws Exception { return run("all"); }

    @Operation(summary = "Session 隔离：Cookie保存、不同客户端、同Cookie跨TCP连接、缺失Cookie")
    @PostMapping("/isolation")
    public Result<SessionLearningDemo.Report> isolation() throws Exception { return run("isolation"); }

    @Operation(summary = "Session 生命周期：更换ID、保留属性、拒绝旧ID、注销与重新创建")
    @PostMapping("/lifecycle")
    public Result<SessionLearningDemo.Report> lifecycle() throws Exception { return run("lifecycle"); }

    @Operation(summary = "Session 过期：真实Servlet空闲超时与过期后重新创建")
    @PostMapping("/expiry")
    public Result<SessionLearningDemo.Report> expiry() throws Exception { return run("expiry"); }

    private Result<SessionLearningDemo.Report> run(String lesson) throws Exception { return Result.success(SessionLearningDemo.run(lesson)); }
}
