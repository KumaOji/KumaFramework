package com.kuma.cloud.lab.p0;

import com.kuma.boot.common.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;

@Tag(name = "MySQL 索引与事务进阶实验")
@RestController
@RequestMapping("/lab/mysql/advanced")
public class MysqlAdvancedLabController {
    private final DataSource source;
    public MysqlAdvancedLabController(DataSource source) { this.source = source; }
    @Operation(summary = "MySQL 完整进阶实验：索引、MVCC、行锁间隙锁、死锁与事务传播")
    @PostMapping("/scenario")
    public Result<LabReport> scenario() throws Exception { return run("all"); }
    @Operation(summary = "EXPLAIN 联合索引、覆盖索引与回表对照")
    @PostMapping("/indexes")
    public Result<LabReport> indexes() throws Exception { return run("indexes"); }
    @Operation(summary = "RC/RR 隔离级别、MVCC 快照读与 FOR UPDATE 当前读")
    @PostMapping("/isolation")
    public Result<LabReport> isolation() throws Exception { return run("isolation"); }
    @Operation(summary = "真实行锁与 RR 间隙锁阻塞、1205超时与回滚释放")
    @PostMapping("/locks")
    public Result<LabReport> locks() throws Exception { return run("locks"); }
    @Operation(summary = "两个事务反向持锁，观察真实1213死锁牺牲者")
    @PostMapping("/deadlock")
    public Result<LabReport> deadlock() throws Exception { return run("deadlock"); }
    @Operation(summary = "Spring REQUIRED/REQUIRES_NEW/NESTED、自调用失效与受检异常回滚")
    @PostMapping("/propagation")
    public Result<LabReport> propagation() throws Exception { return run("propagation"); }
    @Operation(summary = "丢失更新、version冲突检测、有限重试与条件原子UPDATE对照")
    @PostMapping("/optimistic")
    public Result<LabReport> optimistic() throws Exception { return run("optimistic"); }
    private Result<LabReport> run(String lesson) throws Exception { return Result.success(MysqlAdvancedLearningDemo.run(source, lesson)); }
}
