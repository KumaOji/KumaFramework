# Project5 — kuma-cloud-job 分布式任务调度 Worker 示例

## 项目概述

Project5 是 `kuma-cloud-job`——Kuma Cloud 自研分布式任务调度系统——的 **Worker（执行器）** 接入示例。

Worker 是任务执行的最小单元，负责：
- 启动时向 job-server 注册并进行 appName 鉴权
- 暴露 gRPC 接口，接收 job-server 下发的任务调度指令
- 执行业务处理器（Processor）并上报结果
- 定期发送心跳健康状态，供 job-server 做负载感知调度

---

## 系统角色

kuma-cloud-job 由三个角色组成，各司其职：

| 角色 | 模块 | 端口 | 职责 |
|------|------|------|------|
| **job-nameserver** | `kuma-cloud-job-nameserver` | HTTP:8089 / gRPC:9089 | 服务注册与发现。job-server 启动后向其注册自身地址；Worker 通过 nameserver 发现并订阅 job-server |
| **job-server** | `kuma-cloud-job-server` | HTTP:8082 / gRPC:9081 | 调度中心。存储任务元数据，按时间表达式（Cron/FixedRate）触发任务，选择 Worker 节点分发，记录执行实例 |
| **project5 (Worker)** | `kuma-project-project5` | HTTP:8085 / gRPC:9082 | **执行器**。实际执行任务业务逻辑，上报健康状态和执行结果 |

---

## 整体架构

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           kuma-cloud-job 系统                                 │
│                                                                              │
│  job-nameserver (gRPC:9089)                                                  │
│      │                                                                       │
│      │  ① job-server 启动时注册自身 IP:Port                                  │
│      │  ② Worker 订阅 job-server 地址列表                                    │
│      │  ③ job-server 重启/切换时推送新地址给 Worker                           │
│      │                                                                       │
│  job-server (gRPC:9081)                                                      │
│      │                                                                       │
│      │  ④ 按 Cron/FixedRate 触发任务（时间轮）                               │
│      │  ⑤ 选择 Worker 节点（健康度优先 / 随机）                              │
│      │  ⑥ 通过 gRPC 下发任务到 Worker                                        │
│      │                                                                       │
│      │◄──── ⑦ Worker 定期上报心跳健康状态（每 10 秒）                        │
│      │◄──── ⑧ Worker 上报任务执行结果（success/fail）                        │
│                                                                              │
│  project5 - Worker (gRPC:9082)                                               │
│      │                                                                       │
│      │  ⑨ BuiltInSpringProcessorFactory 按 processorInfo 查找处理器          │
│      │  ⑩ 执行 BasicProcessor.process() 或 @KmcJobHandler 方法               │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 端口说明

| 端口 | 归属 | 用途 |
|------|------|------|
| 8085 | project5 HTTP | Spring MVC 接口（`/job/create` 等） |
| 9082 | project5 gRPC | Worker gRPC 服务端，job-server 通过此端口下发任务 |
| 9081 | job-server gRPC | 调度中心 gRPC，Worker 向此端口上报心跳和结果 |
| 8089/9089 | job-nameserver | 服务发现，Worker 通过此端口订阅 job-server 地址 |

---

## Worker 启动流程

```
Spring 容器就绪
    │
    ▼
KmcJobAutoConfiguration（Worker 自动配置）
    │
    ├── assertApp()                       ← 向 job-server 发起 appName 鉴权
    │       └── job-server 查 app_info 表 { app_name='project5' }
    │               ├── 找到 → 返回 appId，Worker 启动继续
    │               └── 找不到 → 抛出异常，Worker 启动失败（需先在 DB 注册 appName）
    │
    ├── gRPC Server 启动（端口 9082）      ← 监听 job-server 的任务下发请求
    │
    ├── BuiltInSpringProcessorFactory 初始化
    │       └── 扫描 Spring 容器中所有 BasicProcessor Bean
    │       └── 扫描所有 @KmcJobHandler 注解方法，包装为 MethodBasicProcessor
    │
    ├── WorkerSubscribeManager 订阅 nameserver
    │       └── 持续接收 job-server 地址列表更新
    │
    └── 启动后台线程
            ├── healthReport（每 10s）    ← 向 job-server 上报负载状态
            └── heartbeatCheck            ← 定期发现 job-server 地址变化
```

---

## 两种处理器写法

### 方式一：实现 `BasicProcessor` 接口（类级处理器）

适合一个 Bean 对应一个任务的场景：

```java
@Component                         // ← 必须是 Spring Bean
public class SimpleJobProcessor implements BasicProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        String jobParams = context.getJobParams();
        log.info("[SimpleJobProcessor] 执行，params={}", jobParams);
        // 业务逻辑...
        return new ProcessResult(true, "success, params=" + jobParams);
    }
}
```

**在 job-server 控制台配置任务时**，`processorInfo` 填写 Bean 名称：

```
processorInfo = simpleJobProcessor
```

**工作原理**：`BuiltInSpringProcessorFactory.build(processorDefinition)` 调用
`applicationContext.getBean("simpleJobProcessor", BasicProcessor.class)` 获取处理器实例，
直接调用 `process()` 方法。

### 方式二：`@KmcJobHandler` 注解（方法级处理器）

适合一个 Bean 内聚合多个不同类型的任务处理逻辑：

```java
@Component
public class MethodJobProcessor {

    @KmcJobHandler(name = "sendNoticeJob")    // name = processorInfo 中填的值
    public void sendNotice(TaskContext context) {
        log.info("[sendNoticeJob] 发送通知，params={}", context.getJobParams());
        // 业务逻辑：发送短信/邮件/推送
    }

    @KmcJobHandler(name = "cleanCacheJob")
    public void cleanCache(TaskContext context) {
        log.info("[cleanCacheJob] 清理缓存，params={}", context.getJobParams());
        // 业务逻辑：清理过期缓存
    }
}
```

**在 job-server 控制台配置任务时**，`processorInfo` 填写注解的 `name` 值：

```
processorInfo = sendNoticeJob   （或 cleanCacheJob）
```

**工作原理**：框架启动时扫描所有 Bean 的方法，找到 `@KmcJobHandler` 注解的方法，
将 `(bean实例, Method对象)` 包装为 `MethodBasicProcessor`，
调用时通过 `method.invoke(bean, context)` 执行，返回值 JSON 序列化为 `ProcessResult.msg`。

### 两种方式对比

| 特性 | `BasicProcessor` 接口 | `@KmcJobHandler` 注解 |
|------|----------------------|----------------------|
| 粒度 | 类级（一个类 = 一个任务） | 方法级（一个类多个任务） |
| processorInfo | Spring Bean 名称 | 注解 `name` 值 |
| 返回值 | `ProcessResult`（明确控制成功/失败） | 方法返回值 JSON 化（void 方法默认成功） |
| 异常处理 | 可 try-catch 后返回 `ProcessResult(false)` | 抛出异常时框架标记为失败 |
| 适用场景 | 逻辑复杂、需要精细控制执行结果 | 简单任务快速接入 |

---

## 核心类说明

### `TaskContext`（任务上下文）

框架注入给处理器的执行上下文，包含本次调度的所有信息：

| 字段 | 类型 | 说明 |
|------|------|------|
| `jobId` | Long | 任务 ID（与 job_info 表对应） |
| `instanceId` | Long | 本次执行实例 ID（每次触发生成唯一值） |
| `subInstanceId` | Long | 子实例 ID（MapReduce 任务使用） |
| `taskId` | String | Task ID（实例内唯一） |
| `taskName` | String | Task 名称 |
| `jobParams` | String | 控制台配置的静态参数 |
| `instanceParams` | String | 运行时动态参数（OpenAPI 触发时传入，或工作流上下文） |
| `maxRetryTimes` | int | 最大重试次数 |
| `currentRetryTimes` | int | 当前重试次数（第一次执行为 0） |
| `subTask` | Object | 子任务对象（MapReduce 场景） |

**常用用法**：

```java
@Override
public ProcessResult process(TaskContext context) throws Exception {
    String params = context.getJobParams();         // 获取控制台配置的参数
    Long jobId = context.getJobId();                // 获取任务 ID
    Long instanceId = context.getInstanceId();      // 获取本次执行实例 ID
    int retry = context.getCurrentRetryTimes();     // 判断是否为重试执行

    if (retry > 0) {
        log.warn("这是第 {} 次重试", retry);
    }
    // ...
    return new ProcessResult(true, "执行成功");
}
```

### `ProcessResult`（执行结果）

```java
// 成功
return new ProcessResult(true, "业务执行成功，处理了 100 条记录");

// 失败（job-server 记录失败原因，可能触发重试）
return new ProcessResult(false, "数据库连接失败：" + e.getMessage());

// 仅返回 boolean（无附加信息）
return new ProcessResult(true);
```

`msg` 字段有长度限制，超长会被截断。建议失败原因保持简洁。

---

## 处理器工厂机制

job-server 下发任务时携带 `processorInfo` 字段。Worker 侧按以下顺序查找处理器：

```
processorInfo = "simpleJobProcessor"
    │
    ▼
BuiltInSpringProcessorFactory.build()
    ├── processorInfo 不含 "#" → 尝试 Spring Bean 查找
    │       └── applicationContext.getBean("simpleJobProcessor")
    │               ├── 找到且实现 BasicProcessor → 直接使用
    │               └── 未找到 → 返回 null，继续下一个工厂
    │
    └── processorInfo 含 "#" → 跳过（由其他工厂处理）

processorInfo = "sendNoticeJob"
    │
    ▼
BuiltInDefaultProcessorFactory.build()   ← 按 @KmcJobHandler name 查找
    └── 从启动时扫描的 name→MethodBasicProcessor Map 中查找
            └── 找到 → 包装为 MethodBasicProcessor 执行 method.invoke(bean, context)
```

`MethodBasicProcessor` 是框架内部类，将 `@KmcJobHandler` 方法适配为 `BasicProcessor` 接口：

```java
class MethodBasicProcessor implements BasicProcessor {
    private final Object bean;
    private final Method method;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        Object result = method.invoke(bean, context);  // 反射调用注解方法
        return new ProcessResult(true, JsonUtils.toJSONString(result));
    }
}
```

---

## 时间表达式类型

在 `JobController.create()` 或 job-server 控制台配置任务时，`timeExpressionType` 决定任务触发方式：

| 类型 | 枚举值 | 时间表达式格式 | 说明 |
|------|--------|--------------|------|
| `CRON` | 0 | Cron 表达式 | 按 Cron 规则定时触发，支持秒级（7 段） |
| `FIXED_RATE` | 1 | 毫秒数 | 固定频率，上次触发后经过指定毫秒立即触发 |
| `FIXED_DELAY` | 2 | 毫秒数 | 固定延迟，上次执行**完成**后经过指定毫秒触发 |
| `DAILY_TIME_INTERVAL` | 3 | 时间区间表达式 | 每天在指定时间区间内按频率触发 |

**Cron 示例**：

```
*/30 * * * * ?    每 30 秒执行一次
0 0 2 * * ?       每天凌晨 2:00 执行
0 0/5 9-18 * * ?  每天 9:00-18:00 之间每 5 分钟执行一次
```

---

## Worker 分发策略

job-server 选择 Worker 节点时支持两种策略（`DispatchStrategy`）：

| 策略 | 说明 |
|------|------|
| `HEALTH_FIRST`（默认） | 优先选择负载较低（轻量任务数最少）的 Worker |
| `RANDOM` | 从可用 Worker 中随机选择 |

Worker 通过心跳上报自身负载信息：
- `maxLightweightTaskNum`：最大轻量任务并发数（默认 1024）
- `maxHeavyweightTaskNum`：最大重量任务并发数（默认 64）
- `currentLightweightTaskNum`：当前正在执行的轻量任务数
- `isOverload`：是否超载（超载时不再接收新任务）

---

## 任务生命周期（job-server 视角）

```
任务定义（JobInfo）
    │
    ▼ Cron/FixedRate 触发
时间轮（TimeWheel）到期
    │
    ▼
DispatchService.dispatch()
    ├── 查询该 appName 下所有健康 Worker（通过 ClusterStatusHolder）
    ├── 按 DispatchStrategy 选择 Worker
    └── 通过 gRPC 发送 ScheduleJobReq → Worker gRPC Server
            │
            ▼
Worker: ServerScheduleGrpcService.scheduleJob()
    │
    ├── 查找处理器（BuiltInSpringProcessorFactory / BuiltInDefaultProcessorFactory）
    ├── 构建 TaskContext
    ├── 线程池执行 processor.process(context)
    └── 执行结果上报给 job-server

job-server: 记录执行实例状态（InstanceInfo）
    ├── success → 状态改为 SUCCEED
    └── fail    → 状态改为 FAILED（可配置重试）
```

---

## OpenAPI 创建任务（`JobController`）

除了在 job-server 控制台手动创建任务，project5 还提供 HTTP 接口通过 `KmcJobTemplate` 动态创建任务：

### `KmcJobTemplate` 工作原理

```java
KmcJobTemplate template = new KmcJobTemplate("127.0.0.1:9089");
// ↑ 连接 job-nameserver，通过 nameserver 路由到 job-server
```

`KmcJobTemplate.createJob()` 内部流程：

```
createJob(JobUpdateReq)
    │
    ├── IdGenerateService.allocate()    ← 雪花算法生成 jobId
    ├── 构建 MqCausa.CreateJobReq（Protobuf）
    │       ├── jobId, appName, jobName, jobDescription
    │       ├── processorInfo（Bean名称或@KmcJobHandler name）
    │       ├── timeExpressionType（CRON/FIXED_RATE/...）
    │       ├── timeExpression（Cron表达式或毫秒数）
    │       ├── lifeCycle（任务生效时间范围，默认永久有效）
    │       └── maxInstanceNum（最大并发实例数）
    │
    └── MessageSendClient.sendMessageAsync()
            └── 通过 gRPC 发送到 job-nameserver
                    └── nameserver 路由到 job-server
                            └── job-server 持久化到 job_info 表
                                    └── 按 timeExpression 开始调度
```

### `JobUpdateReq` 字段说明

| 字段 | 说明 | 示例 |
|------|------|------|
| `appName` | Worker 的应用名，须与 `kmcjob.worker.app-name` 和 DB 中一致 | `"project5"` |
| `jobName` | 任务名称（显示用） | `"simpleJobProcessor-job"` |
| `jobDescription` | 任务描述 | `"每30秒执行一次清理"` |
| `processorInfo` | 处理器标识（Bean名称或@KmcJobHandler name） | `"simpleJobProcessor"` |
| `jobParams` | 传递给处理器的参数（TaskContext.getJobParams()获取） | `"userId=123"` |
| `timeExpressionType` | 时间表达式类型 | `TimeExpressionType.CRON` |
| `timeExpression` | 时间表达式 | `"0/30 * * * * ?"` |
| `lifeCycle` | 任务生命周期（开始/结束时间），`new LifeCycle()` 为永久有效 | `new LifeCycle()` |
| `maxInstanceNum` | 最大同时运行实例数（防止重叠执行）| `1` |

---

## 后台心跳机制

Worker 启动成功后，持续运行两个后台任务：

### 1. 健康状态上报（每 10 秒）

`HeartHealthReportRpcService` 向 job-server gRPC 发送 `WorkerHeartbeat`：

```
[WorkerHealthReporter] report health status
    appId: 1
    appName: project5
    isOverload: false
    maxLightweightTaskNum: 1024
    currentLightweightTaskNum: 0
    maxHeavyweightTaskNum: 64
```

job-server 据此判断 Worker 是否可用、是否超载，在 `ClusterStatusHolder` 中维护 Worker 集群状态。
心跳超时（超过一定时间未收到心跳）的 Worker 会被摘除，不再分发任务。

### 2. 心跳检测（heartbeatCheck）

`ServerDiscoverService.heartbeatCheck()` 定期向 nameserver 查询最新的 job-server 地址列表，
并更新 `WorkerSubscribeManager` 中的 `serverIpList`。当 job-server 重启或主从切换时，
Worker 无需重启即可感知到新地址。

---

## Worker 注册前置条件

### 数据库注册 appName

**job-server 的 `app_info` 表必须存在 project5 的记录**，否则 Worker 启动时 `assertApp()` 鉴权失败。

```sql
INSERT INTO app_info (app_name, current_server) VALUES ('project5', '127.0.0.1');
```

`assertApp()` 流程：
1. Worker 向 job-server 发送 gRPC 请求，携带 `app_name = "project5"`
2. job-server 查询 `app_info` 表
3. 找到记录 → 返回 appId，Worker 继续启动
4. 未找到 → 抛出异常，Worker 启动失败

---

## Project5 代码结构

```
kuma-project-project5/
├── src/main/java/com/kuma/cloud/project5/
│   ├── Project5Application.java           # 启动类
│   ├── job/
│   │   ├── SimpleJobProcessor.java        # 类级处理器（BasicProcessor 接口）
│   │   └── MethodJobProcessor.java        # 方法级处理器（@KmcJobHandler 注解）
│   ├── controller/
│   │   └── JobController.java             # HTTP 接口：动态创建任务
│   └── config/
│       └── Project5Config.java            # Spring 配置（占位）
├── src/main/resources/
│   ├── application.yml                    # 激活 dev profile
│   └── application-dev.yml               # Worker 配置、端口
├── build.gradle                           # Gradle 依赖（job-worker + job-client）
└── JOB.md                                 # 快速参考文档
```

---

## 配置说明

### `application-dev.yml`

```yaml
server:
  port: 8085                            # HTTP 服务端口

kmcjob:
  worker:
    enabled: true                        # 是否启用 Worker
    app-name: project5                   # Worker 应用名（须与 app_info 表一致）
    server-address: 127.0.0.1           # job-server IP（不含端口）
    server-port: 9081                    # job-server gRPC 端口
    name-server-address: 127.0.0.1:9089 # job-nameserver 地址（用于服务发现）
```

### 配置项说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `kmcjob.worker.enabled` | `true` | Worker 开关，false 时不启动 gRPC 服务端，不注册 |
| `kmcjob.worker.app-name` | 无 | Worker 分组标识，必须与 DB `app_info.app_name` 一致 |
| `kmcjob.worker.server-address` | 无 | job-server IP，用于初始连接 |
| `kmcjob.worker.server-port` | `9081` | job-server gRPC 端口 |
| `kmcjob.worker.name-server-address` | 无 | nameserver 地址，格式 `ip:port`，多个用逗号分隔 |

---

## 快速启动

### 依赖服务启动顺序

```
1. 启动数据库（MySQL），执行 SQL 注册 appName：
   INSERT INTO app_info (app_name, current_server) VALUES ('project5', '127.0.0.1');

2. 启动 job-nameserver（端口 9089）

3. 启动 job-server（HTTP:8082, gRPC:9081）

4. 启动 project5（HTTP:8085, gRPC:9082）
```

### 启动命令

```bash
./gradlew :kuma-project:kuma-project-project5:bootRun
```

### 启动成功日志

```
[KmcJob] starting ...
GrpcServer started, listening on 9082
[KmcJobSubscribeService] subscribe success
[WorkerHealthReporter] report health status, appId:1, appName:project5, isOverload:false
```

---

## API 接口

**Base URL**: `http://localhost:8085`

### 动态创建任务

```http
GET /job/create?processorInfo=simpleJobProcessor&cron=0/30 * * * * ?&jobParams=hello
```

**参数**：

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `processorInfo` | `simpleJobProcessor` | Bean 名称或 @KmcJobHandler name |
| `cron` | `0/30 * * * * ?` | Cron 表达式（每30秒一次） |
| `jobParams` | `test` | 传递给 TaskContext.getJobParams() 的参数 |

**返回示例**：
```
jobId=1866025432917831680
```

**创建 @KmcJobHandler 任务**：
```http
GET /job/create?processorInfo=sendNoticeJob&cron=0 0 9 * * ?&jobParams=userId=100
```

**创建 cleanCacheJob 任务**：
```http
GET /job/create?processorInfo=cleanCacheJob&cron=0 30 1 * * ?&jobParams=region=all
```

---

## 启动日志说明

| 日志 | 含义 | 处理建议 |
|------|------|----------|
| `[KmcJob] starting ...` | Worker 开始初始化 | 正常 |
| `GrpcServer started, listening on 9082` | Worker gRPC 服务端就绪，等待 job-server 下发任务 | 正常 |
| `[KmcJobSubscribeService] subscribe success` | 已向 nameserver 订阅 job-server 地址 | 正常 |
| `[WorkerHealthReporter] ... isOverload:false` | 健康上报成功，Worker 可接收任务 | 正常 |
| `assertApp failed: app not found` | app_info 表中无 project5 记录 | 执行注册 SQL |
| `Connect to job-server failed` | job-server 未启动或地址配置错误 | 检查 `server-address` 和 `server-port` |

---

## 常见问题

**Q: Worker 启动时报 `assertApp` 鉴权失败？**

A: job-server 数据库的 `app_info` 表中没有 `app_name='project5'` 的记录。
执行：`INSERT INTO app_info (app_name, current_server) VALUES ('project5', '127.0.0.1');`

**Q: 任务触发了但处理器没有执行？**

A: 检查以下几点：
- `processorInfo` 与 Bean 名称或 `@KmcJobHandler(name=...)` 的 name 是否一致（大小写敏感）
- `SimpleJobProcessor` 是否加了 `@Component` 注解（必须是 Spring 管理的 Bean）
- Worker gRPC 端口（9082）是否被防火墙或其他进程占用

**Q: 同一个任务是否可能被同时触发多次？**

A: `maxInstanceNum` 控制最大并发实例数。设置 `maxInstanceNum=1` 可防止上次未执行完时再次触发。
job-server 在分发前会检查当前运行中的实例数量。

**Q: 处理器抛出异常会怎样？**

A: 框架会捕获异常，向 job-server 上报 `ProcessResult(false, exceptionMessage)`，
job-server 记录执行失败。如果配置了重试次数，会在一定延迟后重新下发任务，
`TaskContext.getCurrentRetryTimes()` 会递增。

**Q: 如何让任务每天只在特定时间段执行？**

A: 使用 Cron 表达式限定时间范围：
```
0 0/10 9-18 * * ?   每天 9:00-18:59 之间每 10 分钟执行
0 30 8 * * MON-FRI  每个工作日早上 8:30 执行
```

**Q: `KmcJobTemplate` 传入 nameserver 地址还是 job-server 地址？**

A: 传入 **nameserver 地址**（`127.0.0.1:9089`），`KmcJobTemplate` 内部通过 nameserver 路由到 job-server。
nameserver 地址即 `kmcjob.worker.name-server-address` 配置的值。

**Q: Worker 可以横向扩展吗？**

A: 可以。同一个 `appName` 下启动多个 Worker 实例（不同端口/机器），
job-server 会按 `DispatchStrategy`（健康度优先/随机）在多个 Worker 间分发任务，
实现负载均衡。每个 Worker 的心跳健康上报让 job-server 实时感知可用节点。
