# Reference 日志、审计与指标

入口：[Reference 开发模板观测](http://localhost:3000/d/kuma-reference)。面板配置在 [grafana-dashboard-reference.yaml](../../k8s/local/base/grafana-dashboard-reference.yaml)，通过现有 Grafana provisioning 加载。

## 接入约定

| 能力 | 复用方式 | 观察内容 |
|---|---|---|
| 操作审计 | `audit-log` starter、Controller 的 `@AuditLog` | 创建、确认订单的操作人、数据范围、结果、失败原因及耗时 |
| 业务指标 | `metrics` starter、ServiceImpl 的 `@TimedMethod` | 创建、确认、详情、分页的调用量、成功、失败与耗时 |
| HTTP 与运行资源 | Actuator、`prometheus` starter | 请求量、状态码、P95、JVM、GC、CPU |
| 连接池与线程池 | Micrometer，`ObservabilityConfiguration` | 两种数据源的连接占用、等待、超时，业务线程池活跃数和队列 |
| 日志 | Logger starter 的 `logback-observability.xml` | JSON 日志、请求 MDC、Loki 查询 |
| 链路 | OTel starter、SkyWalking Agent | OTLP 导出的请求链路，Agent 的自动探针与异步上下文 |

`@AuditLog` 放在同步写操作的 Controller 上，以保留请求信息；业务事务完成后生成成功记录。确认操作的 `bizId` 为 `scope:订单ID`，创建操作的返回结果中包含新 ID。参数校验在方法执行前失败时不会进入审计切面，可从 HTTP 400 指标定位。

审计复用默认的 `Slf4jAuditLogStore`，输出到 `com.kuma.boot.auditlog.AUDIT`，经 Loki 保存。模板设置 `async: false`，在请求线程生成日志，保留请求与链路 MDC；Loki appender 自身批量发送。此示例不额外建立审计数据库表。

默认操作人来自 `X-User-Id`、`X-User-Name`，仅用于匿名开发演示。接入认证后，通过自定义 `AuditOperatorProvider` 从可信认证上下文获取操作人。订单入参不录入审计，示例返回值会录入；新增业务需按数据敏感性选择 `recordParams`、`recordResult`。

## 本机演示

按 [启动说明](README.md#启动) 准备 Nacos、数据库和连接环境变量后，在仓库根目录启动。

```powershell
$env:KUMA_CONFIG_PROFILE = "dev"
$env:SPRING_PROFILES_ACTIVE = "dev,observability"
$env:SERVER_ADDRESS = "0.0.0.0"
.\gradlew.bat :kuma-project:kuma-project-reference:bootRun
```

应用监听 `0.0.0.0:19092`，供 WSL Prometheus 抓取。需要 SkyWalking Agent 时，传入下文的 `-PskywalkingAgent` 参数。

Prometheus 的本机抓取任务为 `reference`，目标 `reference-local:19092/actuator/prometheus`。在 WSL 中执行以下命令注册 Windows 宿主地址；网络地址变化后重新执行：

```bash
python3 scripts/register-services-metrics-wsl.py "$(ip -4 route show default | awk '{print $3; exit}')" reference
```

Loki 日志需要激活 `observability` profile；`no-loki` profile 可关闭远程日志。通过 `KUMA_LOKI_URL`、`REFERENCE_OTLP_ENDPOINT` 分别调整日志与链路接收地址，`KUMA_ENVIRONMENT` 设置环境标签。使用 Nacos 常规启动时，同步发布配置模板，并用 `KUMA_CONFIG_PROFILE=dev` 固定 Data ID 后再追加 `observability` profile，避免将多个 profile 拼进 Data ID。

使用 [订单接口](README.md#接口) 分别创建和确认 MAIN、MYSQL 订单，生成审计、日志和指标。

## 链路追踪与日志上下文

Nacos 模板已配置 `management.tracing` 和 Boot 4 的 `management.opentelemetry.tracing.export.otlp`。默认发送到 `http://localhost:4318/v1/traces`，对应 WSL 已转发的 OTEL Collector；Collector 再转发到 SkyWalking OAP。开发环境默认采样率为 1.0，可通过 `REFERENCE_TRACING_SAMPLING` 调整，通过 `REFERENCE_OTLP_ENDPOINT` 修改接收地址，通过 `REFERENCE_TRACING_ENABLED` 控制追踪。

直接使用 Gradle 启动时可传入 `-PskywalkingAgent=<Agent JAR 绝对路径>`，可选 `-PskywalkingBackend=localhost:11800`。IDEA 则在 ReferenceApplication 的 VM options 中加入：

```text
-javaagent:<Agent JAR 绝对路径>
-Dskywalking.agent.service_name=kuma-reference
-Dskywalking.collector.backend_service=localhost:11800
```

SkyWalking UI 为 `http://localhost:12880`。请求订单接口后，业务日志中的 `OtlpTraceId` 应有 traceId/spanId，加载 Agent 后请求线程的 `TID` 应有值；两套追踪的 ID 可能不同。启动日志没有请求上下文时仍可能为空。只使用 OpenTelemetry 导出也可以在 SkyWalking 查看追踪，但不会自动填充 SkyWalking toolkit 的 `%tid`。

WebConfiguration 显式引入 WebMdcAutoConfiguration，由框架统一注册 TraceFilter、TenantFilter、VersionFilter，均支持 Servlet 异步请求。`kmc-tenant-id` 请求头或同名参数会写入日志 MDC，未提供时使用框架默认租户 `1`；`kmc-request-version` 请求头会写入 RequestVersion，未提供时为空。可通过 `kuma.boot.web.filter.trace=false`、`kuma.boot.web.filter.tenant=false` 或 `kuma.boot.web.filter.version=false` 关闭对应过滤器。项目提供对应 Filter Bean 或 FilterRegistrationBean 时，框架会退让，避免重复注册。日志中的请求租户用于关联上下文，订单数据库路由仍由 DataScope 白名单控制。

三个过滤器同时处理 REQUEST 和 ASYNC 分派：异步任务的结果或异常回到 Servlet 线程时，重新建立 MDC，并在该次分派结束后清理。框架生成的 TraceId 和解析后的请求租户保存在请求属性中，因此初始请求与异步异常日志使用同一个关联 ID，后续请求不会沿用上次的租户或版本。

core 的异步池会组合框架上下文装饰器与应用提供的 TaskDecorator。reference 提供 SkyWalking RunnableWrapper 装饰器，保证其业务异步任务延续请求的 SkyWalking 链路；未加载 Agent 时该包装器不会生成 SkyWalking ID。Klog 字段读取 MDC 的 `tl`，当前框架中的 KlogAutoConfiguration 和 KlogIdGenerator 未实现实际 Klog 接入，因此该字段仍为空。

## 面板读法

- **HTTP**：查看接口请求速率、4xx/5xx 和 P95，定位请求失败与耗时。
- **业务变化**：`createOrder` 成功表示新增订单，`confirmOrder` 成功表示 `CREATED → CONFIRMED`；失败按方法与异常类型统计。校验失败只计入 HTTP 指标。
- **运行资源**：同时观察 JVM、GC、数据库连接池与业务线程池，区分业务冲突与资源等待。
- **审计日志**：展开 `message` 查看操作人、`scope`、结果及耗时，确认记录可按 `bizId` 查找；外层 `traceId` 关联请求链路。
- **调用链**：展开日志的 `traceId` 跳转 SkyWalking，或进入调用链工作台选择 `kuma-reference`。这里使用 OTel trace ID；SkyWalking Agent 自身链路 ID 可能不同。

核心指标为 `reference_business_calls_total`、`reference_business_calls_success_total`、`reference_business_calls_error_total` 和 `reference_business_execution_time_seconds_*`。业务指标由框架方法切面记录，表示本进程观察到的调用；不是订单数据库存量，也不用于金额对账。原始计数在进程重启后重置，面板用 `increase`、`rate` 展示时间范围内的变化。

指标标签只使用有限的方法、异常、环境等维度；订单 ID、SKU、操作人和 Trace ID 放在日志中。Prometheus 默认每 15 秒采样，趋势至少需要两个采样点；无数据表示尚无对应序列或采集异常，不能直接当作零。

参考：[Spring Boot 指标](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)、[Grafana provisioning](https://grafana.com/docs/grafana/latest/administration/provisioning/)、[Loki 日志关联](https://grafana.com/docs/grafana/latest/datasources/loki/configure/)。
